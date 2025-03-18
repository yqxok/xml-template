package com.yonyou.jb.bip.controller.util.xml;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValueResolverComposite implements InsertValueIResolver {
    private List<InsertValueIResolver> resolvers;
    public ValueResolverComposite(){
        resolvers=new ArrayList<>();
    }
    public ValueResolverComposite(InsertValueIResolver... resolvers){
        this.resolvers= Arrays.asList(resolvers);
    }
    @Override
    public boolean resolveValue(Node node) {
        for (InsertValueIResolver resolver : resolvers) {
            if (resolver.resolveValue(node)) {
                return true;
            }
        }
        return false;
    }
//    public static ValueResolverComposite getDefaultInstance(){
//        return new ValueResolverComposite(new JsonInserValueResolver())
//    }
}
