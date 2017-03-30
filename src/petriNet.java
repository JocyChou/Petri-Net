//Student name: Siyu Zhou
//Student ID number:77729957

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class petriNet {
	public static void main(String[] args) throws FileNotFoundException{
		HashMap<String,transition> tran = new HashMap<String,transition>();  //initiate array for storage
		HashMap<String,place> pl = new HashMap<String,place>();
		
		String filename = args[0];      //get file name
		int cycle = Integer.valueOf(args[1]);   //get cycle number
		
		File text = new File(System.getProperty("java.class.path") + "/" + filename);  //find the file read and parse it
		Scanner scnr = new Scanner(text);
		int lineNumber = 1;
        while(scnr.hasNextLine()){
            String line = scnr.nextLine();  //read each line
            String[] array = line.split(" ");   //parse each line
            //put different input into different hashmaps
            if(array[0].equals("place")){  //place
            	place elepl = new place();
            	elepl.name = array[1];
            	elepl.setToken(Integer.valueOf(array[2]));
            	pl.put(array[1],elepl);
            }
            else if(array[0].equals("transition")){   //transition
            	transition eletran = new transition();
            	eletran.name = array[1];
            	tran.put(array[1], eletran);
            }
            else if(array[0].equals("edge")){   //edge
            	if(pl.containsKey(array[1])){   //input edge for transition
            		edge ed = new edge();
            		boolean add = false;
            		ed.place = pl.get(array[1]);
            		ed.transition = tran.get(array[2]);
            		ed.weight = 1;
            		for(edge e : tran.get(array[2]).input){
            			if(ed.place.name.equals(e.place.name) && ed.transition.name.equals(e.transition.name)){
            				e.weight++;
            				add = true;
            			}
            		}
            		if(add == false){
            			tran.get(array[2]).addInput(ed);
            		}
            	}
            	if(tran.containsKey(array[1])){              	//output edge for transition
            		edge ed = new edge();
            		boolean add = false;
            		ed.place = pl.get(array[2]);
            		ed.transition = tran.get(array[1]);
            		ed.weight = 1;
            		for(edge e : tran.get(array[1]).output){
            			if(ed.place.name.equals(e.place.name) && ed.transition.name.equals(e.transition.name)){
            				e.weight++;
            				add = true;
            			}
            		}
            		if(add == false){
            			tran.get(array[1]).addOutput(ed);
            		}
            	}
            }
            lineNumber++;
        }
        while(cycle > 0){   //with the limit of cycle number
        	int fail = 0;
        	for(String trans : tran.keySet()){    //find all the transitions if there is one can fire
        		if(tran.get(trans).canFire()){
        			tran.get(trans).fire();
        			System.out.println("The result after fire on " + tran.get(trans).name +" :");
        			for(String name : pl.keySet()){
        				int value = pl.get(name).token;
        				System.out.println("place " + name + " has token:" + value);
        			}
        			break;
        		}else{
        			fail++;	  //if all transitions can not fire this petri-net is deadlock		
        		}
        	}
        	if(fail == tran.size()){
        		System.out.println("Sorry, there is no transition can fire!");
        		break;
        	}
        	cycle--;
        }
        if(cycle == 0){
        	System.out.println("Finishes the required cycle execution!");
        }
        System.out.println("Petri-net finishes iterating!");
	}
}
class transition{
	String name;
	ArrayList<edge> input = new ArrayList<edge>();
	ArrayList<edge> output = new ArrayList<edge>();
	
	boolean canFire(){  //judge if this transition can be fired
		boolean result = true;
		if(this.input.isEmpty() && this.output.isEmpty()){    //if this is a invalid transition
			result = false;
		}
		for(edge ed : this.input){    //test all the input edges 
			result = result & ed.canInFire();
		}
		return result;
	}
	
	void fire(){
		for(edge ed : this.input){    //file 
			ed.inFire();
		}
		for(edge ed : this.output){
			ed.outFire();
		}
	}
	
	void addInput(edge ed){
		this.input.add(ed);
	}
	
	void addOutput(edge ed){
		this.output.add(ed);
	}
}
class edge{
	place place;
	transition transition;
	int weight;
	
	boolean canInFire(){       //check if this input edge can fire
		boolean result = true;
		if(!this.place.isEnough(this.weight)){
			result = false;
		}
		return result;
	}
	
	void inFire(){
		this.place.deleteToken(this.weight);
	}
	
	void outFire(){
		this.place.addToken(this.weight);
	}
}
class place{
	String name;
	int token;
	
	int get(){
		return this.token;
	}
	
	boolean isEnough(int min){   //check if the place has enough token for the edge
		boolean result = false;
		if(token >= min){
			result = true;
		}
		return result;
	}
	
	void setToken(int tnum){        //set token number when store
		token = tnum;
	}
	
	void addToken(int tnum){     //add token after transition
		token = token + tnum;
	}
	
	void deleteToken(int tnum){
		token = token - tnum;
	}
}