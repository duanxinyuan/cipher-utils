package com.dxy.common.util;

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
import java.util.ArrayList;
import java.util.List;

/**
 * xml操作工具类（基于DOM的XML分析器）
 * @author duanxinyuan
 * 2017/11/14 21:24
 */
public interface XmlUtil {

    /**
     * 获取所有节点
     * @param file xml文件地址<p>
     * 获取节点名称：node.getNodeName()<br/>
     * 获取节点的值：node.getTextContent()
     */
    static List<Node> getNodes(File file) {
        return getNodes(getDocument(file));
    }

    /**
     * 获取所有节点
     * @param xmlText xml内容
     */
    static List<Node> getNodes(String xmlText) {
        return getNodes(getDocument(xmlText));
    }

    /**
     * 获取所有节点
     * @param document dom对象dom文档
     */
    static List<Node> getNodes(Document document) {
        List<Node> nodeList = new ArrayList<>();
        Element root = null;
        if (document != null) {
            root = document.getDocumentElement();
        }
        NodeList list = null;
        if (root != null) {
            list = root.getChildNodes();
        }
        if (list != null) {
            for (int i = 0; i < list.getLength(); i++) {
                getNodes(list.item(i), nodeList);
            }
        }
        return nodeList;
    }

    /**
     * 获取Document对象
     * @param obj File的xml文件, 或者String类型的xml内容
     */
    static Document getDocument(File obj) {
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
    static Document getDocument(String obj) {
        //dom解析工厂
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //dom解析器
            DocumentBuilder db = dbf.newDocumentBuilder();
            String xmlText = String.valueOf(obj);
            StringReader read = new StringReader(xmlText);
            InputSource source = new InputSource(read);
            return db.parse(source);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 递归遍历node节点
     */
    static void getNodes(Node node, List<Node> list) {
        if (StringUtils.isEmpty(node.getNodeName()) || "#text".equals(node.getNodeName())) {
            return;
        }
        list.add(node);
        NodeList nlt = node.getChildNodes();
        if (nlt.getLength() > 0) {
            for (int i = 0; i < nlt.getLength(); i++) {
                getNodes(nlt.item(i), list);
            }
        }
    }

    /**
     * 该方法解析xml为实体类核心操作, obj为xml文件地址或者xml内容,
     * 此参数类型为File或者String, 其它类型返回null.
     * @param obj xml文件或者xml内容
     * @param clazz 实体类的clazz
     * @param dateFormat xml中日期格式
     */
    static Object parseToEntity(Object obj, Class<?> clazz, String dateFormat) {
        try {
            Object o = clazz.newInstance();

            List<Node> nodeList;
            if (obj instanceof File) {
                nodeList = getNodes((File) obj);
            } else if (obj instanceof String) {
                nodeList = getNodes((String) obj);
            } else {
                return null;
            }
            for (Node aNodeList : nodeList) {
                String nodeName = aNodeList.getNodeName();
                String nodeText = aNodeList.getTextContent();
                Field field = clazz.getDeclaredField(nodeName);
                String type = field.getType().getName();
                Object value = ReflectUtil.parseClassName(nodeText, type, dateFormat);
                ReflectUtil.setValueByProperty(o, nodeName, value);
            }
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将xml解析为实体类, <b>该实体类必须为public修饰</b>, 否则报错<p>
     * xml文件必须只能有两级节点, xml二级节点名称实体类中必须有同名属性, 该属性要有set方法,
     * dateFormat为xml中的日期格式, 此参数为null时, 默认使用"yyyy-MM-dd HH:mm:ss"格式
     * @param file xml文件地址
     * @param clazz 实体类clazz
     * @return 实体类
     */
    static Object parseToEntity(File file, Class<?> clazz, String dateFormat) {
        return parseToEntity(file, clazz, dateFormat);
    }

    /**
     * 将xml解析为实体类, <b>该实体类必须为public修饰</b>, 否则报错<p>
     * xml文件必须只能有两级节点, xml二级节点名称实体类中必须有同名属性, 该属性要有set方法,
     * dateFormat为xml中的日期格式, 此参数为null时, 默认使用"yyyy-MM-dd HH:mm:ss"格式
     * @param xmlText xml内容
     * @param clazz 实体类clazz
     * @return 实体类
     */
    static Object parseToEntity(String xmlText, Class<?> clazz, String dateFormat) {
        return parseToEntity(xmlText, clazz, dateFormat);
    }

    /**
     * xml转换为实体类核心操作, 该方法通过获取全部节点, 对每个节点进行判断,
     * 是实体类节点时, 创建对象, 接下来的其它节点进行为对象属性赋值,
     * 直到下一个实习类节点, 该方法要注意二级节点的名称要和
     * entityNodeName同名, 如果entityNodeName为空, 二级节点要和类名同名,
     * 三级节点名称要在实体类中有同名属性对应, 该属性要有set方法.
     * @param o xml文件或者xml内容
     * @param clazz 实体类, 该实体类为public修饰
     * @param entityNodeName 实体类节点,二级节点
     * @param dateFormat xml中的日期格式
     * @return 实体类集合
     */
    static List<?> parseToEntity(Object o, Class<?> clazz, String entityNodeName, String dateFormat) {
        List<Object> list = new ArrayList<>();
        List<Node> nodeList;
        if (o instanceof File) {
            nodeList = getNodes((File) o);
        }
        if (o instanceof String) {
            nodeList = getNodes((String) o);
        } else {
            return null;
        }
        int index = -1;
        if (entityNodeName == null) {
            entityNodeName = clazz.getSimpleName();
        }
        try {
            for (Node aNodeList : nodeList) {
                String nodeName = aNodeList.getNodeName();
                if (nodeName.equals(entityNodeName)) {
                    list.add(clazz.newInstance());
                    index++;
                    continue;
                }
                String nodeText = aNodeList.getTextContent();
                Field field = clazz.getDeclaredField(nodeName);
                String type = field.getType().getName();
                Object value = ReflectUtil.parseClassName(nodeText, type, dateFormat);
                ReflectUtil.setValueByProperty(list.get(index), nodeName, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将xml解析为实体类集合, xml内容有三级节点, 二级节点为实体类, 三级节点为实体类属性
     * 必须保证 实体类三级节点名称在实体类中都有同名属性, 该属性要求有set方法
     * @param file xml文件地址
     * @param clazz 实体类, <b>该实体类必须为public修饰</b>
     * @param entityNodeName 二级节点名称, 即为实体分割节点, 该值为空是默认为实体类类名
     */
    static List<?> parseToEntity(File file, Class<?> clazz, String entityNodeName, String dateFormat) {
        return parseToEntity(file, clazz, entityNodeName, dateFormat);
    }

    /**
     * 将xml解析为实体类集合, xml内容有三级节点, 二级节点为实体类, 三级节点为实体类属性
     * 必须保证 实体类三级节点名称在实体类中都有同名属性, 该属性要求有set方法
     * @param xmlText xml内容
     * @param clazz 实体类, <b>该实体类必须为public修饰</b>
     * @param entityNodeName 二级节点名称, 即为实体分割节点, 该值为空是默认为实体类类名
     */
    static List<?> parseToEntity(String xmlText, Class<?> clazz, String entityNodeName, String dateFormat) {
        return parseToEntity(xmlText, clazz, entityNodeName, dateFormat);
    }

    /**
     * 递归 转换为json字符串
     */
    static String parseToJson(Node node) {
        if (StringUtils.isEmpty(node.getNodeName()) || "#text".equals(node.getNodeName())) {
            return "";
        }
        StringBuilder text = new StringBuilder("\"" + node.getNodeName() + "\":");
        NodeList nlt = node.getChildNodes();
        if (null != nlt) {
            if (nlt.getLength() > 1) {
                text.append("{");
                for (int i = 0; i < nlt.getLength(); i++) {
                    text.append(parseToJson(nlt.item(i)));
                }
                if (text.toString().endsWith(",")) {
                    int end = text.length() - 1;
                    text = new StringBuilder(text.substring(0, end));
                }
                text.append("},");
            } else {
                if (node.getFirstChild() != null && StringUtils.isNotEmpty(node.getFirstChild().getNodeValue()) && node.getFirstChild().getNodeValue().startsWith("{")) {
                    text.append(node.getTextContent()).append(",");
                } else {
                    text.append("\"").append(node.getTextContent()).append("\",");
                }
            }
        }
        return text.toString();
    }

    /**
     * 将xml转为json字符串
     */
    static String parseToJson(File file) {
        return parseToJson(getDocument(file));
    }

    /**
     * 将xml转为json字符串
     * @param xmlText xml内容
     */
    static String parseToJson(String xmlText) {
        return parseToJson(getDocument(xmlText));
    }

    /**
     * 将xml转为json字符串
     * @param document dom对象dom文档
     */
    static String parseToJson(Document document) {
        Element root = null;
        if (document != null) {
            root = document.getDocumentElement();
        }
        NodeList list = null;
        if (root != null) {
            list = root.getChildNodes();
        }
        StringBuilder jsonBuilder = new StringBuilder("{");
        if (list != null) {
            for (int i = 0; i < list.getLength(); i++) {
                jsonBuilder.append(parseToJson(list.item(i)));
            }
        }
        String json = jsonBuilder.toString();
        if (json.endsWith(",")) {
            int end = json.length() - 1;
            json = json.substring(0, end);
        }
        return json + "}";
    }


}
