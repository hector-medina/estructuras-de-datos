package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.traversal.Iterator;

import java.util.Comparator;

public class OrganizingEntity implements Comparable<OrganizingEntity> {
    private String organizationId;
    private String description;
    private String name;
    private List<SportEvent> events;
    private int attenders;
    public static final Comparator<OrganizingEntity> CMP_A = (o1, o2)->Integer.compare(o1.numAttenders(), o2.numAttenders());
    public OrganizingEntity(String organizationId, String name, String description) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        events = new LinkedList<>();
        attenders = 0;
    }

    public String getName() {
        return name;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getDescription() {
        return description;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Iterator<SportEvent> activities() {
        return events.values();
    }

    public void addEvent(SportEvent sportEvent) {
        events.insertEnd(sportEvent);
    }

    public int numEvents() {
        return events.size();
    }

    public boolean hasActivities() {
        return events.size() > 0;
    }

    public Iterator<SportEvent> sportEvents() {
        return events.values();
    }

    public int numAttenders(){
        return attenders;
    }
    public void addAttender(){
        attenders++;
    }

    @Override
    public int compareTo(OrganizingEntity o) {
        return Integer.compare(this.attenders, o.numAttenders());
    }
}
