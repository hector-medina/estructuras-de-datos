package uoc.ds.pr.model;

import edu.uoc.ds.adt.helpers.Position;
import edu.uoc.ds.adt.nonlinear.HashTable;
import edu.uoc.ds.adt.nonlinear.PriorityQueue;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.adt.sequential.Queue;
import edu.uoc.ds.adt.sequential.QueueArrayImpl;
import edu.uoc.ds.traversal.Iterator;
import edu.uoc.ds.traversal.Traversal;
import uoc.ds.pr.SportEvents4Club;

import javax.swing.text.TableView;
import java.time.LocalDate;
import java.util.Comparator;


import static uoc.ds.pr.SportEvents4Club.MAX_NUM_ENROLLMENT;

public class SportEvent implements Comparable<SportEvent> {
    public static final Comparator<SportEvent> CMP_V = (se1, se2)->Double.compare(se1.rating(), se2.rating());
    public static final Comparator<String> CMP_K = (k1, k2)-> k1.compareTo(k2);

    private String eventId;
    private String description;
    private SportEvents4Club.Type type;
    private LocalDate startDate;
    private LocalDate endDate;
    private int max;

    private File file;

    private List<Rating> ratings;
    private double sumRating;

    private int numSubstitutes;

    private Queue<Enrollment> enrollments;
    private HashTable<String, Attender> attenders;
    private LinkedList<Worker> workers;
    private OrganizingEntity organizingEntity;
    private Queue<Enrollment> substitutes;


    public SportEvent(String eventId, String description, SportEvents4Club.Type type,
                      LocalDate startDate, LocalDate endDate, int max, File file, OrganizingEntity organizingEntity) {
        setEventId(eventId);
        setDescription(description);
        setStartDate(startDate);
        setEndDate(endDate);
        setType(type);
        setMax(max);
        setFile(file);
        this.enrollments = new QueueArrayImpl<>(MAX_NUM_ENROLLMENT);
        this.ratings = new LinkedList<>();
        numSubstitutes = 0;
        attenders = new HashTable<>();
        workers = new LinkedList<>();
        this.organizingEntity = organizingEntity;
        this.substitutes = new PriorityQueue<>(Enrollment.CMP_E);
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SportEvents4Club.Type getType() {
        return type;
    }

    public void setType(SportEvents4Club.Type type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    public double rating() {
        return (this.ratings.size()>0?(sumRating / this.ratings.size()):0);
    }

    public void addRating(SportEvents4Club.Rating rating, String message, Player player) {
        Rating newRating = new Rating(rating, message, player);
        ratings.insertEnd(newRating);
        sumRating+=rating.getValue();
    }

    public boolean hasRatings() {
        return ratings.size()>0;
    }

    public Iterator<Rating> ratings() {
        return ratings.values();
    }


    public void addEnrollment(Player player) {
        addEnrollment(player, false);
    }

    public void addEnrollment(Player player, boolean isSubstitute) {
        if(isSubstitute){
            substitutes.add(new Enrollment(player, isSubstitute));
        }
        else {
            enrollments.add(new Enrollment(player, isSubstitute));
        }
    }

    public boolean is(String eventId) {
        return this.eventId.equals(eventId);
    }

    @Override
    public int compareTo(SportEvent se2) {
        return Double.compare(rating(), se2.rating() );
    }

    public boolean isFull() {
        return (enrollments.size()>=max);
    }

    public int numPlayers() {
        return enrollments.size()+substitutes.size();
    }

    public void incSubstitutes() {
        numSubstitutes++;
    }

    public void addEnrollmentAsSubstitute(Player player) {
        addEnrollment(player, true);
        incSubstitutes();
    }

    public int getNumSubstitutes() {
        return numSubstitutes;
    }

    public OrganizingEntity getOrganizingEntity(){
        return organizingEntity;
    }

    public int numAttenders(){
        return attenders.size();
    }

    public Attender getAttender(String phone){
        return attenders.get(phone);
    }

    public void addAttender(String phone, String name){
        Attender attender = new Attender(phone, name);
        attenders.put(phone, attender);
    }

    public boolean hasWorker(Worker worker){
        boolean hasWorker = false;
        Traversal<Worker> workers = this.workers.positions();
        Position<Worker> position;
        while(!hasWorker && workers.hasNext()){
            position = workers.next();
            hasWorker = position.getElem().equals(worker);
        }
        return hasWorker;
    }

    public void addWorker(Worker worker){
        workers.insertEnd(worker);
    }

    public int numWorkers(){
        return workers.size();
    }

    public Iterator<Worker> getWorkers(){
        return workers.values();
    }

    public Iterator<Attender> getAttenders(){
        return attenders.values();
    }

    public Iterator<Enrollment> getSubstitutes(){
        return substitutes.values();
    }
}
