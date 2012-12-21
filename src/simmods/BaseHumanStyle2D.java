package simmods;

import java.awt.Color; 
import java.awt.Font;
import java.awt.Paint; 
import java.awt.Stroke; 

import agents.BaseHuman;
import repast.simphony.visualization.visualization2D.style.DefaultStyle2D; 
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;


public class BaseHumanStyle2D extends DefaultStyleOGL2D 
{ 
        //@Override 
        /*public Color getColor(Object o) 
        { 
                if(o instanceof BaseHuman) { 
                	BaseHuman agent = (BaseHuman) o; 
                        if(agent.isSmoker()) { 
                                return Color.RED; 
                        } 
                        else { 
                                return Color.CYAN; 
                        } 
                } 
                else { 
                        return null; 
                } 
        }*/
        
        @Override
        public String getLabel(Object o)
        {
        	if(o instanceof BaseHuman) { 
        		BaseHuman agent = (BaseHuman) o;
	        	//System.out.println("Calling! " + agent.getID());
	    
	        	return agent.getID();
        	}
        	return null;
        }
        
        @Override
        public Color getLabelColor(Object object) {
            return Color.BLACK;
         }
        
        @Override
        public Font getLabelFont(Object object) {
            return new Font("Arial", Font.PLAIN, 12);
          }
} 