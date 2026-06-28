package com.olc1.ast.stm;

import java.util.ArrayList;
import java.util.List;

public class CaseNodes {
    private final List<CaseNode> cases;

    public CaseNodes() {
        this.cases = new ArrayList<>();
    }

    public CaseNodes(CaseNode caseNode) {
        this.cases = new ArrayList<>();
        add(caseNode);
    }

    public void add(CaseNode caseNode) {
        if (caseNode != null) {
            this.cases.add(caseNode);
        }
    }

    public List<CaseNode> getCases() {
        return cases;
    }
}