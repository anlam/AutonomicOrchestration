package eu.arrowhead.autonomic.orchestrator.manager.plan.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.reasoner.rulesys.ClauseEntry;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.PrintUtil;
import org.jose4j.json.internal.json_simple.JSONObject;

public class OrchestrationRuleRegister {

    private String systemName;
    private List<String> rules;

    public OrchestrationRuleRegister() {

    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public void setRawRules(List<Rule> rules) {
        List<String> rulesStr = new ArrayList<String>();
        for (Rule r : rules) {
            rulesStr.add(rule2Json(r));
        }
        this.rules = rulesStr;
    }

    public String rule2Json(Rule rule) {
        JSONObject obj = new JSONObject();

        List<String> head = new ArrayList<String>();
        for (ClauseEntry aHead : rule.getHead()) {
            head.add(PrintUtil.print(aHead));
        }

        List<String> body = new ArrayList<String>();
        for (ClauseEntry aBody : rule.getBody()) {
            body.add(PrintUtil.print(aBody));
        }

        obj.put("rule_name", rule.getName());
        obj.put("head", head);
        obj.put("body", body);
        return obj.toJSONString();
    }
}
