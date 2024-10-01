package com.typeahead.trie_microservice.pojo;

import org.springframework.stereotype.Component;

@Component
public class Trie {
    public Node root; 

    public Trie(){
        root = new Node();
    }

    public void getPrefix(String word){
        Node curNode = root;
        for(char curChar : word.toCharArray()){
            if(!curNode.map.containsKey(curChar)) {
                System.out.println("No popular prefixes");
            }
            else{
                System.out.println(curNode.mostPopularWordsWithPrefix.toString());
                curNode = curNode.map.get(curChar);
            }
        }
    }

    public void addWord(String word){
        Node curNode = root;
        for(char curChar : word.toCharArray()){
            if(!curNode.map.containsKey(curChar)){
                curNode.mostPopularWordsWithPrefix.add(word);
                curNode.map.put(curChar, new Node());
            }
            curNode = curNode.map.get(curChar);
        }
        curNode.isEndOfWord = true;
    }
}
