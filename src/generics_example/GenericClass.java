package generics_example;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class GenericClass extends AbstractClass{


    @Override
    public <T> void printGenericArray(ArrayList<T> obj) {


        ArrayList<ModelClass1> modelClass1s = (ArrayList<ModelClass1>) obj;
        if(modelClass1s != null){
            System.out.println(modelClass1s.get(0).getName());
        }
        /*ArrayList<ModelClass2> modelClass2s = (ArrayList<ModelClass2>) obj;
        if(modelClass2s != null){
            System.out.println(modelClass2s.get(0).getName());
        }*/

        System.out.println("Object size is: " + obj.size());
    }

    public static void main(String[] args){

        ArrayList<ModelClass1> modelClass1 = new ArrayList<>();
        modelClass1.add(new ModelClass1("Test Victim Model Class 1"));

        GenericClass genericClass = new GenericClass();

        genericClass.printGenericArray(modelClass1);


    }





}
