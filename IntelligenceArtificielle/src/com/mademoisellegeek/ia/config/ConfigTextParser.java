/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mademoisellegeek.ia.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author ahmedboussadia
 */
public class ConfigTextParser {
    private String host ;
    private String teamName;
    private int port = 0;
    private String textFilePath;
    
    public ConfigTextParser(String filePath){
        textFilePath = filePath;
        
        //lecture du fichier texte	
        try{
                InputStream ips=new FileInputStream(textFilePath); 
                InputStreamReader ipsr=new InputStreamReader(ips);
                BufferedReader br=new BufferedReader(ipsr);
                String ligne;
                while ((ligne=br.readLine())!=null){
                        System.out.println(ligne);
                        if(host == null)
                            host = ligne;
                        else if(port == 0)
                            port = Integer.parseInt(ligne);
                        else if(teamName == null){
                            teamName = ligne;
                            break;
                        }
                }
                br.close(); 
        }		
        catch (Exception e){
                System.out.println(e.toString());
        }
    }
    
    public String getHost(){
        return host;
    }
    
    public int getPort(){
        return port;
    }
    
    public String getName(){
        return teamName;
    }
}
