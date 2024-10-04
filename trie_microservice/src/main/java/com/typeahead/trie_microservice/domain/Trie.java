package com.typeahead.trie_microservice.domain;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Trie implements TrieInterface{
    public Node root; 

    public Trie(){
        root = new Node();
    }

    public List<String> getPrefix(String prefix){
        Node curNode = root;
        for(char curChar : prefix.toCharArray()){
            if(!curNode.map.containsKey(curChar)) return Collections.emptyList();
            else curNode = curNode.map.get(curChar);
        }
        return (curNode.mostPopularWordsWithPrefix != null) ? curNode.mostPopularWordsWithPrefix : Collections.emptyList();
    }

    public void addPrefix(String prefix){
        Node curNode = root;
        for(char curChar : prefix.toCharArray()){
            if(!curNode.map.containsKey(curChar)){
                curNode.mostPopularWordsWithPrefix.add(prefix);
                curNode.map.put(curChar, new Node());
            }
            curNode = curNode.map.get(curChar);
        }
        curNode.isEndOfWord = true;
    }
}
