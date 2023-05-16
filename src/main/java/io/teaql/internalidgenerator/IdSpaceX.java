package io.teaql.internalidgenerator;

import io.teaql.idspace.idspace.IdSpace;

public class IdSpaceX extends IdSpace {


    public IdSpace increase(){
        if(this.getCurrent()==null){
            this.updateCurrent(this.getCurrent()+1);
            return this;
        }
        this.updateCurrent(this.getCurrent()+1);
        return this;
    }

    @Override
    public  String typeName(){
        return "IdSpace";
    }



}
