

package org.example.xmljsonconverter;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class ManualConverterService {

    // MODIFICATION 1 : Le regex capture maintenant les attributs dans le groupe 2
    // Groupe 1: Nom de la balise
    // Groupe 2: Les attributs (ex: ' id="1" type="test"')
    // Groupe 3: Le contenu
    private static final String TAG_PATTERN = "<([\\w:.-]+)([^>]*)>(.*?)</\\1>";
    private static final Pattern XML_TAG = Pattern.compile(TAG_PATTERN, Pattern.DOTALL);

    // Regex pour extraire clé="valeur" dans la chaîne d'attributs
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("([\\w:.-]+)=\"([^\"]*)\"");

    public String xmlToJsonManual(String xml) {
        if (xml == null || xml.trim().isEmpty()) return "ERREUR : Champ vide.";

        String input = xml.replaceAll("<\\?xml.*?\\?>", "")
                .replaceAll("", "").trim();

        // Regex Racine adapté pour capturer les attributs de la racine aussi
        Matcher rootMatcher = Pattern.compile("^<([\\w:.-]+)([^>]*)>(.*)</\\1>$", Pattern.DOTALL).matcher(input);

        if (!rootMatcher.matches()) {
            return "ERREUR : XML invalide (Pas de racine unique ou structure mal formée).";
        }

        String rootName = rootMatcher.group(1);
        String rootAttributes = rootMatcher.group(2); // Attributs de la racine
        String content = rootMatcher.group(3).trim();

        try {
            // 1. On parse le contenu récursivement
            Object parsedData = parseXmlRecursive(content);

            // 2. On ajoute les attributs de la racine si nécessaire
            Map<String, Object> rootAttrMap = parseAttributes(rootAttributes);
            if (!rootAttrMap.isEmpty()) {
                if (parsedData instanceof Map) {
                    ((Map<String, Object>) parsedData).putAll(rootAttrMap);
                } else if (parsedData instanceof String) {
                    // Si la racine n'a que du texte mais aussi des attributs
                    Map<String, Object> newRoot = new LinkedHashMap<>(rootAttrMap);
                    newRoot.put("content", parsedData);
                    parsedData = newRoot;
                }
            }

            return "{\n  \"" + rootName + "\": " + formatAsJson(parsedData, "  ") + "\n}";
        } catch (Exception e) {
            return "ERREUR MANUELLE : " + e.getMessage();
        }
    }

    private Object parseXmlRecursive(String content) {
        content = content.trim();

        if (content.startsWith("<![CDATA[") && content.endsWith("]]>")) {
            return content.substring(9, content.length() - 3);
        }

        // Si pas de balises enfants, c'est du texte brut
        if (!content.contains("<") || content.indexOf("<") > content.lastIndexOf(">")) {
            return content;
        }

        Map<String, Object> map = new LinkedHashMap<>();
        Matcher matcher = XML_TAG.matcher(content);

        boolean foundTag = false;

        while (matcher.find()) {
            foundTag = true;
            String tagName = matcher.group(1);
            String attrString = matcher.group(2); // Capture des attributs
            String innerContent = matcher.group(3).trim();

            // 1. Récupération récursive du contenu de l'enfant
            Object childValue = parseXmlRecursive(innerContent);

            // 2. Parsing des attributs de l'enfant (ex: id="12")
            Map<String, Object> attributes = parseAttributes(attrString);

            // 3. FUSION : Contenu + Attributs
            Object finalValue;
            if (attributes.isEmpty()) {
                finalValue = childValue;
            } else {
                // Si l'enfant est déjà un objet (Map), on ajoute les attributs dedans
                if (childValue instanceof Map) {
                    ((Map<String, Object>) childValue).putAll(attributes);
                    finalValue = childValue;
                }
                // Si l'enfant est du texte simple, on doit créer un objet pour contenir texte + attributs
                else {
                    Map<String, Object> complexObject = new LinkedHashMap<>(attributes);
                    // On n'ajoute le contenu texte que s'il n'est pas vide
                    if (!childValue.toString().isEmpty()) {
                        complexObject.put("content", childValue); // On stocke le texte dans "content"
                    }
                    finalValue = complexObject;
                }
            }

            // Gestion des listes (si la clé existe déjà)
            if (map.containsKey(tagName)) {
                Object existing = map.get(tagName);
                if (existing instanceof List) {
                    ((List<Object>) existing).add(finalValue);
                } else {
                    List<Object> list = new ArrayList<>();
                    list.add(existing);
                    list.add(finalValue);
                    map.put(tagName, list);
                }
            } else {
                map.put(tagName, finalValue);
            }
        }

        return foundTag ? map : content;
    }

    // NOUVELLE METHODE : Transforme string 'id="1" val="2"' en Map {id:1, val:2}
    private Map<String, Object> parseAttributes(String attributeString) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        if (attributeString == null || attributeString.trim().isEmpty()) return attributes;

        Matcher m = ATTRIBUTE_PATTERN.matcher(attributeString);
        while (m.find()) {
            // Groupe 1 : Nom de l'attribut, Groupe 2 : Valeur
            // On peut ajouter un préfixe "@" si on veut les distinguer, ex: "@id"
            // Ici on met juste le nom brut comme demandé.
            attributes.put(m.group(1), m.group(2));
        }
        return attributes;
    }

    private String formatAsJson(Object obj, String indent) {
        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            String nextIndent = indent + "  ";
            return "{\n" + map.entrySet().stream()
                    .map(e -> nextIndent + "\"" + e.getKey() + "\": " + formatAsJson(e.getValue(), nextIndent))
                    .collect(Collectors.joining(",\n")) + "\n" + indent + "}";
        } else if (obj instanceof List) {
            List<Object> list = (List<Object>) obj;
            return "[" + list.stream().map(o -> formatAsJson(o, indent)).collect(Collectors.joining(", ")) + "]";
        } else {
            String s = String.valueOf(obj);
            s = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
            if (s.matches("-?\\d+(\\.\\d+)?") || s.equals("true") || s.equals("false")) return s;
            return "\"" + s + "\"";
        }
    }

    // ... La méthode jsonToXmlManual reste inchangée ou celle de la réponse précédente ...
    public String jsonToXmlManual(String json) {
        if (json == null || json.trim().isEmpty()) return "ERREUR : Champ vide.";
        String input = json.trim();
        String rootName;
        String content;
        Pattern p = Pattern.compile("^\\{\\s*\"([\\w:.-]+)\"\\s*:\\s*\\{(.*)\\}\\s*\\}$", Pattern.DOTALL);
        Matcher m = p.matcher(input);
        if (m.matches()) {
            rootName = m.group(1);
            content = m.group(2).trim();
        } else if (input.startsWith("{") && input.endsWith("}")) {
            rootName = "root";
            content = input.substring(1, input.length() - 1).trim();
        } else {
            return "ERREUR : JSON invalide.";
        }
        try {
            return "<" + rootName + ">\n" + jsonToXmlRecursive(content, "  ") + "</" + rootName + ">";
        } catch (Exception e) {
            return "ERREUR : Structure JSON mal formée.";
        }
    }

    private String jsonToXmlRecursive(String content, String indent) {
        StringBuilder xml = new StringBuilder();
        Pattern pattern = Pattern.compile("\"([\\w:.-]+)\"\\s*:\\s*(\\{.*?\\}|\\[.*?\\]|\".*?\"|[^,\\s\\}]+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String key = matcher.group(1);
            String val = matcher.group(2).trim();
            if (val.startsWith("{")) {
                String inner = val.substring(1, val.length() - 1).trim();
                xml.append(indent).append("<").append(key).append(">\n")
                        .append(jsonToXmlRecursive(inner, indent + "  "))
                        .append(indent).append("</").append(key).append(">\n");
            } else if (val.startsWith("[")) {
                String[] items = val.substring(1, val.length() - 1).split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                for (String item : items) {
                    xml.append(indent).append("<").append(key).append(">").append(cleanValue(item)).append("</").append(key).append(">\n");
                }
            } else {
                xml.append(indent).append("<").append(key).append(">");
                String cleanVal = cleanValue(val);
                if (cleanVal.contains("<") || cleanVal.contains("&")) xml.append("<![CDATA[").append(cleanVal).append("]]>");
                else xml.append(cleanVal);
                xml.append("</").append(key).append(">\n");
            }
        }
        return xml.toString();
    }

    private String cleanValue(String val) {
        return val.trim().replaceAll("^\"|\"$", "").replace("\\\"", "\"").replace("\\n", "\n");
    }
}