package uoc.ds.pr.model;

import edu.uoc.ds.adt.helpers.Position;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.traversal.Iterator;
import edu.uoc.ds.traversal.Traversal;

public class Role {
    private String roleId;
    private String description;
    private LinkedList<Worker> workers;
    public Role(String roleId, String description){
        this.roleId = roleId;
        this.description = description;
        workers = new LinkedList<>();

    }
    public String getRoleId(){
        return roleId;
    }
    public String getDescription(){
        return description;
    }

    @Override
    public boolean equals(Object object){
        Role role = (Role) object;
        return roleId.equals(role.getRoleId());
    }
    public int getNumWorkers(){
        return workers.size();
    }

    public void addWorker(Worker worker){
        workers.insertEnd(worker);
    }

    public boolean containsWorker(Worker worker){
        edu.uoc.ds.traversal.Iterator<Worker> it = this.workers.values();
        boolean found = false;
        while(!found && it.hasNext()){
            if(worker.getDni().equals(it.next().getDni())){
                found = true;
            }
        }
        return found;
    }

    public void deleteWorker(Worker worker){
        edu.uoc.ds.traversal.Iterator<Worker> it = this.workers.values();
        Traversal<Worker> pos = this.workers.positions();
        boolean found = false;
        while(!found && it.hasNext()){
            Worker w = it.next();
            Position p = pos.next();
            if(worker.getDni().equals(w.getDni())){
                workers.delete(p);
                found = true;
            }
        }
    }

    public Iterator<Worker> getWorkers(){
        return workers.values();
    }

}
