/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knapsackalgorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author KANZA SYED
 */
public class KnapsackAlgorithm {
    static int capacity;
    static int max;
    static int iter;
    static boolean stop; 
    static ArrayList<ArrayList<Float>> itemList=new ArrayList<>();
    static ArrayList<ArrayList<Integer>> population=new ArrayList<>();
    static ArrayList<ArrayList<ArrayList<Integer>>> generation=new ArrayList<>();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        readFileInList();
        System.out.println("Enter the capacity of kanpsack");
        Scanner inp=new Scanner(System.in);
        capacity=inp.nextInt();
        System.out.println("Enter no of iterations");
        inp=new Scanner(System.in);
        iter=inp.nextInt();
        System.out.println(itemList);
        initPopulation();
        evolution();
    }
     
    public static void readFileInList(){ 
        try{ 
            BufferedReader br = new BufferedReader(new FileReader("input.txt")); 
  
            String st; 
            br.readLine();
            while ((st = br.readLine()) != null){
                ArrayList<Float> item=new ArrayList<>();
                String[] split=st.split(",");
                item.add(Float.parseFloat(split[0]));
                item.add(Float.parseFloat(split[1]));
                itemList.add(item);
            }
            br.close();
        }catch (IOException e){
        // do something
        e.printStackTrace();
        }  
    }
    
    public static int[] calcFitness(ArrayList<Integer> chromosome){
       int[] fitness=new int[2];
        for (int i = 0; i < itemList.size(); i++) {
            /*WEIGHT*/
            fitness[0]+=chromosome.get(i)*itemList.get(i).get(0);
            /*VALUE*/
            fitness[1]+=chromosome.get(i)*itemList.get(i).get(1);
        }
        if(fitness[0]>capacity){
            fitness[0]=0; fitness[1]=0;
        }
        return fitness;
    }
    
    public static void initPopulation(){
        ArrayList<Integer> chromosome=new ArrayList<>(itemList.size()+2);
        for (int i = 0; i <itemList.size() ; i++) {
            chromosome=new ArrayList<>(itemList.size()+2);
            for (int j = 0; j < itemList.size(); j++) {
                chromosome.add(getrandomBinary());
            }
            int[] fitness=calcFitness(chromosome);
            chromosome.add(fitness[0]);
            chromosome.add(fitness[1]);
            population.add(chromosome);
        }
        generation.add(population);
        sorting(population);
        max=population.get(0).get(itemList.size());
    }
    public static int[] parentSelection(){
        Random rand=new Random();
        int[] parent=new int[2];
        parent[0]=rand.nextInt(population.size());
        parent[1]=rand.nextInt(population.size());
        if(parent[0]!=parent[1]){
            return parent;
        }
        else if(parent[0]==parent[1]){
            parentSelection();
        }
        return null;
       
    }
    public static int crossoverPoint(){
        Random rand=new Random();
        int max=itemList.size()-1;
        int crossoverPoint=rand.nextInt(max);
        return crossoverPoint;
    }
    public static int mutationPoint(){
           Random rand=new Random();
        int mutationPoint=rand.nextInt(itemList.size());
        return mutationPoint;
    }
    public static void mutate(ArrayList<Integer> child){
        int mutationpoint=mutationPoint();
        if(child.get(mutationpoint)==0){
            child.set(mutationpoint, 1);
        }
        else if(child.get(mutationpoint)==1){
            child.set(mutationpoint, 0);
        }
        
    }
    
    /*SIngle Child Generation*/
    public static void childGeneration(){
        int[] parent=parentSelection();
        if(parent!=null){

        ArrayList<ArrayList<Integer>> children=new ArrayList<>();

        ArrayList<Integer> chromosome1=new ArrayList<>();
        ArrayList<Integer> chromosome2=new ArrayList<>();

        chromosome1.addAll(population.get(parent[0]));
        chromosome2.addAll(population.get(parent[1]));

        children.add(chromosome1);
        children.add(chromosome2);

        int crossoverpoint=crossoverPoint()+1;

        ArrayList<Integer> tempCH1=new ArrayList<>();
        ArrayList<Integer> tempCH2=new ArrayList<>();

        for (int i = crossoverpoint; i < itemList.size(); i++) {
            tempCH1.add(children.get(0).get(i));
            tempCH2.add(children.get(1).get(i));
        }
        
       for (int i = 0,j=crossoverpoint; i <=tempCH1.size()&& j<itemList.size();j++, i++) {
           children.get(0).set(j, tempCH2.get(i));
           children.get(1).set(j, tempCH1.get(i));
           
        }
       int[] fitness=calcFitness(children.get(0));
       children.get(0).set(itemList.size(), fitness[0]);
       children.get(0).set(itemList.size()+1, fitness[1]);
       
       fitness=calcFitness(children.get(1));
       children.get(1).set(itemList.size(), fitness[0]);
       children.get(1).set(itemList.size()+1, fitness[1]);
       
      /*RANDOM MUTATION*/
      /*GET RANDOM CHILD TO PERFROM MUATION*/
      int childNo=getrandomBinary();
      mutate(children.get(childNo));
      
      fitness=calcFitness(children.get(childNo));
       children.get(childNo).set(itemList.size(), fitness[0]);
       children.get(childNo).set(itemList.size()+1, fitness[1]);
        
        population.add(children.get(0));
        population.add(children.get(1));
            
        children.clear();
        
        }
        else{
        childGeneration();
        }    
    }
    
    
    /*Multiple Child Generation*/
    public static void childPopulator(){
        for (int i = 0; i < itemList.size()/2; i++) {
            childGeneration();
        }
        sorting(population);
        evaluateMAX(population.get(0).get(itemList.size()+1));
        
         updatepopulation();
    }
    
    public static void evolution(){
        while( iter>0){
        childPopulator(); iter--;
            System.out.println("\n\nGENERATION "+ generation.size());
          print(generation.get(generation.size()-1));
        }
        System.out.println("\n\nFINAL ANSWER\n\n");
        print(generation.get(generation.size()-1));
    }
    
    public static void updatepopulation(){
         ArrayList<ArrayList<Integer>> temp=new ArrayList<>();
         for (int i = 0; i < itemList.size(); i++) {
            temp.add(population.get(i));
        }
        generation.add(generation.size(),temp);
    }
    public static void evaluateMAX(int localMAX){
        if(localMAX>max){
            max=localMAX;
            stop=false;
        }
        else if(localMAX==max){
            stop=true;
        }
    }
    public static int getrandomBinary(){
        Random rand=new Random();
        int binary=rand.nextInt((1 - 0) + 1) + 0;
        return binary;
    }

    public static void print(ArrayList<ArrayList<Integer>> finalPop){
        System.out.println("Selected Elements");
        for (int i = 0; i < itemList.size(); i++) {
            if((finalPop.get(0).get(i)*itemList.get(i).get(0))!=0){
                System.out.println("Weight: "+itemList.get(i).get(0) +", Fitness: "+itemList.get(i).get(1));
            }
        }
        System.out.println("No of iterations: "+iter);
        System.out.println("TOTAL WEIGHT: "+finalPop.get(0).get(itemList.size())+", MAXIMUM FITNESS: "+finalPop.get(0).get(itemList.size()+1));
       
    } 
    public static void sorting( ArrayList<ArrayList<Integer>> pop){
    Comparator<ArrayList<Integer>> myComparator = new Comparator<ArrayList<Integer>>() {
        @Override
        public int compare(ArrayList<Integer> o1, ArrayList<Integer> o2) {
            return o1.get(itemList.size()+1).compareTo(o2.get(itemList.size()+1));
        }
    };
        Collections.sort(population, myComparator.reversed());
    
    }
}
