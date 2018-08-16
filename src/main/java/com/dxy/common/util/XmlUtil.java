package com.dxy.common.util;

import com.dxy.library.json.GsonUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * xml操作工具类（基于DOM的XML分析器）
 * 注：最大可支持二级嵌套，例如List<T>、Map<K,V>，不支持三级或更高的嵌套（例如Map<K,List<V>>）
 * @author duanxinyuan
 * 2017/11/14 21:24
 */
public class XmlUtil {

    /**
     * 将xml转为对象
     */
    public static <T> T from(File file, Class<T> c) {
        String from = from(getDocument(file), c);
        if (StringUtils.isEmpty(from)) {
            return null;
        }
        return GsonUtil.lenientFrom(from, c);
    }

    /**
     * 将xml转为对象
     * @param xml xml内容
     */
    public static <T> T from(String xml, Class<T> c) {
        String from = from(getDocument(xml), c);
        if (StringUtils.isEmpty(from)) {
            return null;
        }
        return GsonUtil.lenientFrom(from, c);
    }

    /**
     * 将xml转为json字符串
     * @param document dom对象dom文档
     */
    private static <T> String from(Document document, Class<T> c) {
        if (document == null) {
            return null;
        }
        Element root = document.getDocumentElement();
        if (root == null) {
            return null;
        }
        if (ClassUtil.isPrimitiveType(c)) {
            if (c == String.class) {
                return root.getNodeValue();
            } else {
                return null;
            }
        }
        NodeList list = root.getChildNodes();
        return "{" + from(list, c) + "}";
    }

    /**
     * 循环解析子节点列表
     */
    private static <T> String from(NodeList nodeList, Class<T> c) {
        String from;
        StringBuilder listBuilder = new StringBuilder();
        for (int j = 0; j < nodeList.getLength(); j++) {
            Node fromItem = nodeList.item(j);
            if (isNodeValid(fromItem)) {
                String listFrom = from(fromItem, c);
                if (StringUtils.isNotEmpty(listFrom)) {
                    listBuilder.append(listFrom);
                    if (j != nodeList.getLength() - 1) {
                        listBuilder.append(",");
                    }
                }
            }
        }
        from = listBuilder.toString();
        if (from.endsWith(",")) {
            from = from.substring(0, from.length() - 1);
        }
        return from;
    }

    /**
     * 解析单个子节点
     */
    private static <T> String from(Node node, Class<T> c) {
        String nodeName = node.getNodeName();
        if (!isNodeValid(node)) {
            return "";
        }
        if (ClassUtil.isPrimitiveType(c)) {
            //基础类型
            return fromPrimitiveType(node, c);
        }
        Field field;
        try {
            field = c.getDeclaredField(nodeName);
        } catch (NoSuchFieldException e) {
            return "";
        }
        Class<?> childType = field.getType();
        if (ClassUtil.isPrimitiveType(childType)) {
            //基础类型
            return fromPrimitiveType(node, childType);
        }
        if (!node.hasChildNodes()) {
            //非基础类型，说明必须有子节点
            return "";
        }
        NodeList childNodes = node.getChildNodes();
        //是否是List
        boolean isList = ClassUtil.isAssignableFrom(childType, List.class);
        //是否是Map
        boolean isMap = ClassUtil.isAssignableFrom(childType, Map.class);

        StringBuilder jsonBuilder = new StringBuilder("\"" + nodeName + "\":");
        if (isList) {
            jsonBuilder.append("[");
        } else {
            jsonBuilder.append("{");
        }
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            String from;
            if (isList) {
                //List
                Type fc = field.getGenericType();
                Type pt = ((ParameterizedType) fc).getActualTypeArguments()[0];
                from = fromGenericType(item, pt, true);
                if (StringUtils.isNotEmpty(from)) {
                    jsonBuilder.append("{");
                    jsonBuilder.append(from);
                    jsonBuilder.append("}");
                }
            } else if (isMap) {
                //Map
                Type fc = field.getGenericType();
                Type pt = ((ParameterizedType) fc).getActualTypeArguments()[1];
                from = fromGenericType(item, pt, false);
                if (StringUtils.isNotEmpty(from)) {
                    jsonBuilder.append(from);
                }
            } else {
                //对象
                from = from(item, childType);
                if (StringUtils.isNotEmpty(from)) {
                    jsonBuilder.append(from);
                }
            }
            if (StringUtils.isNotEmpty(from) && i != childNodes.getLength() - 1) {
                jsonBuilder.append(",");
            }
        }
        String json = jsonBuilder.toString();
        if (json.endsWith(",")) {
            json = json.substring(0, json.length() - 1);
        }
        if (isList) {
            json += "]";
        } else {
            json += "}";
        }
        return json;
    }

    /**
     * 解析泛型
     */
    private static String fromGenericType(Node item, Type pt, boolean isList) {
        String from = "";
        if (pt instanceof Class) {
            Class<?> genericClazz = (Class) pt;
            if (ClassUtil.isPrimitiveType(genericClazz)) {
                from = from(item, genericClazz);
            } else {
                //非基础类型，即嵌套对象，遍历有效的子节点
                if (isList) {
                    //List嵌套对象
                    if (item.hasChildNodes()) {
                        NodeList fromChildNodes = item.getChildNodes();
                        from = from(fromChildNodes, genericClazz);
                    }
                } else {
                    //Map嵌套对象
                    if (isNodeValid(item) && item.hasChildNodes()) {
                        NodeList fromChildNodes = item.getChildNodes();
                        //解析二级子节点
                        StringBuilder mapBuilder = new StringBuilder();
                        for (int j = 0; j < fromChildNodes.getLength(); j++) {
                            Node fromItem = fromChildNodes.item(j);
                            if (isNodeValid(fromItem) && fromItem.hasChildNodes()) {
                                //拼接Key
                                mapBuilder.append("\"").append(item.getNodeName()).append("\":");
                                NodeList mapChildNodes = fromItem.getChildNodes();
                                mapBuilder.append("{").append(from(mapChildNodes, genericClazz)).append("}");
                            }
                        }
                        from = mapBuilder.toString();
                        if (from.endsWith(",")) {
                            from = from.substring(0, from.length() - 1);
                        }
                    }
                }
            }
        } else {
            //非class，说明是map或者List
            Type[] actualTypeArguments = ((ParameterizedType) pt).getActualTypeArguments();
            Type actualTypeArgument;
            if (actualTypeArguments.length > 1) {
                //Map
                actualTypeArgument = actualTypeArguments[1];
            } else {
                //List
                actualTypeArgument = actualTypeArguments[0];
            }
            from = from(item, (Class) actualTypeArgument);
        }
        return from;
    }

    /**
     * 解析基础类型字段
     */
    private static String fromPrimitiveType(Node node, Class<?> c) {
        String textContent = node.getTextContent();
        if (StringUtils.isEmpty(textContent)) {
            return "";
        }
        StringBuilder text = new StringBuilder("\"" + node.getNodeName() + "\":");
        if (c == String.class) {
            if (textContent.startsWith("{")) {
                //有大括号的字符串，说明已经是个json，不需要再包双引号
                text.append(textContent);
            } else {
                text.append("\"").append(textContent).append("\"");
            }
        } else {
            Object from = GsonUtil.from(textContent, c);
            if (from != null) {
                text.append(from);
            }
        }
        return text.toString();
    }

    /**
     * 获取Document对象
     * @param obj File的xml文件, 或者String类型的xml内容
     */
    private static Document getDocument(File obj) {
        //dom解析工厂
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //dom解析器
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(obj);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取Document对象
     * @param obj File的xml文件, 或者String类型的xml内容
     */
    private static Document getDocument(String obj) {
        //dom解析工厂
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //dom解析器
            DocumentBuilder db = dbf.newDocumentBuilder();
            String xml = String.valueOf(obj);
            StringReader read = new StringReader(xml);
            InputSource source = new InputSource(read);
            return db.parse(source);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据节点名称判断是否有效
     */
    private static boolean isNodeValid(Node node) {
        String nodeName = node.getNodeName();
        return StringUtils.isNotEmpty(nodeName) && !"#text".equals(nodeName);
    }

}
