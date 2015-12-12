package org.hyperion.map;


import java.util.ArrayList;

public class DirectionCollection {

    public ArrayList<Integer> directions = new ArrayList<Integer>();
    public int face;

    public DirectionCollection(final ArrayList<Integer> toadd, final int face) {
        this.directions.clear();
        this.face = face;
        for(final int i : toadd){
            this.directions.add(i);
        }
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((directions == null) ? 0 : directions.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        final DirectionCollection other = (DirectionCollection) obj;
        if(directions == null){
            if(other.directions != null)
                return false;
        }else if(!directions.equals(other.directions))
            return false;
        return true;
    }

}
