package uoc.ds.pr;

import edu.uoc.ds.adt.nonlinear.Dictionary;
import edu.uoc.ds.adt.nonlinear.DictionaryAVLImpl;
import edu.uoc.ds.adt.nonlinear.HashTable;
import edu.uoc.ds.adt.nonlinear.PriorityQueue;
import edu.uoc.ds.traversal.Iterator;

import java.time.LocalDate;
import uoc.ds.pr.util.*;
import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.*;

public class SportEvents4ClubImpl implements SportEvents4Club {
    private Player mostActivePlayer;
    private Dictionary<String, SportEvent> sportEvents;
    private Dictionary<String, Player> players;
    private OrderedVector<SportEvent> bestSportEvent;
    private HashTable<String,OrganizingEntity> organizingEntities;
    private PriorityQueue<File> files;
    private int totalFiles;
    private int rejectedFiles;
    private Role[] roles;
    private int numroles;
    private HashTable<String, Worker> workers;
    private SportEvent bestSportEventByAttenders;
    private OrderedVector<OrganizingEntity> best5OrganizingEntities;

    public SportEvents4ClubImpl(){
        organizingEntities = new HashTable<>(MAX_NUM_ORGANIZING_ENTITIES);
        files = new PriorityQueue<>(File.CMP);
        mostActivePlayer = null;
        sportEvents = new DictionaryAVLImpl<>();;
        players = new DictionaryAVLImpl<>();
        bestSportEvent = new OrderedVector<>(MAX_NUM_SPORT_EVENTS, SportEvent.CMP_V);
        roles = new Role[MAX_ROLES];
        numroles = 0;
        workers = new HashTable<>();
        best5OrganizingEntities = new OrderedVector<>(MAX_ORGANIZING_ENTITIES_WITH_MORE_ATTENDERS, OrganizingEntity.CMP_A);

    }

    @Override
    public void addPlayer(String id, String name, String surname, LocalDate dateOfBirth) {
        Player u = getPlayer(id);
        if (u != null) {
            u.setName(name);
            u.setSurname(surname);
            u.setBirthday(dateOfBirth);
        } else {
            u = new Player(id, name, surname, dateOfBirth);
            players.put(id, u);
        }
    }

    @Override
    public void addOrganizingEntity(String id, String name, String description) {
        OrganizingEntity organizingEntity = getOrganizingEntity(id);
        if (organizingEntity != null) {
            organizingEntity.setName(name);
            organizingEntity.setDescription(description);
        } else {
            organizingEntity = new OrganizingEntity(id, name, description);
            organizingEntities.put(id, organizingEntity);
        }
    }

    @Override
    public void addFile(String id, String eventId, String orgId, String description, Type type, byte resources, int max, LocalDate startDate, LocalDate endDate) throws OrganizingEntityNotFoundException {
        if (!organizingEntities.containsKey(orgId)) {
            throw new OrganizingEntityNotFoundException();
        }
        OrganizingEntity organization = getOrganizingEntity(orgId);
        if (organization == null) {
            throw new OrganizingEntityNotFoundException();
        }

        files.add(new File(id, eventId, description, type, startDate, endDate, resources, max, organization));
        totalFiles++;
    }

    @Override
    public File updateFile(Status status, LocalDate date, String description) throws NoFilesException {
        File file = files.poll();
        if( file == null ){
            throw new NoFilesException();
        }

        file.update(status, date, description);
        if(file.isEnabled()){
            SportEvent sportEvent = file.newSportEvent();
            sportEvents.put(sportEvent.getEventId(), sportEvent);
        }
        else {
            rejectedFiles++;
        }
        return file;
    }

    @Override
    public void signUpEvent(String playerId, String eventId) throws PlayerNotFoundException, SportEventNotFoundException, LimitExceededException {
        Player player = getPlayer(playerId);
        if (player == null) {
            throw new PlayerNotFoundException();
        }

        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }

        player.addEvent(sportEvent);
        if (!sportEvent.isFull()) {
            sportEvent.addEnrollment(player);
        }
        else {
            sportEvent.addEnrollmentAsSubstitute(player);
            throw new LimitExceededException();
        }
        updateMostActivePlayer(player);
    }

    @Override
    public double getRejectedFiles() {
        return (double) rejectedFiles / totalFiles;
    }

    @Override
    public Iterator<SportEvent> getSportEventsByOrganizingEntity(String organizationId) throws NoSportEventsException {
        OrganizingEntity organizingEntity = getOrganizingEntity(organizationId);

        if (organizingEntity==null || !organizingEntity.hasActivities()) {
            throw new NoSportEventsException();
        }
        return organizingEntity.sportEvents();
    }

    @Override
    public Iterator<SportEvent> getAllEvents() throws NoSportEventsException {
        Iterator<SportEvent> it = sportEvents.values();
        if (!it.hasNext()) throw new NoSportEventsException();
        return it;
    }

    @Override
    public Iterator<SportEvent> getEventsByPlayer(String playerId) throws NoSportEventsException {
        Player player = getPlayer(playerId);
        if (player==null || !player.hasEvents()) {
            throw new NoSportEventsException();
        }
        Iterator<SportEvent> it = player.getEvents();

        return it;
    }

    @Override
    public void addRating(String playerId, String eventId, Rating rating, String message) throws SportEventNotFoundException, PlayerNotFoundException, PlayerNotInSportEventException {
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }

        Player player = getPlayer(playerId);
        if (player == null) {
            throw new PlayerNotFoundException();
        }

        if (!player.isInSportEvent(eventId)) {
            throw new PlayerNotInSportEventException();
        }

        sportEvent.addRating(rating, message, player);
        player.addRating(rating, message);
        updateBestSportEvent(sportEvent);
    }

    @Override
    public Iterator<uoc.ds.pr.model.Rating> getRatingsByEvent(String eventId) throws SportEventNotFoundException, NoRatingsException {
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent  == null) {
            throw new SportEventNotFoundException();
        }

        if (!sportEvent.hasRatings()) {
            throw new NoRatingsException();
        }

        return sportEvent.ratings();
    }

    @Override
    public Player mostActivePlayer() throws PlayerNotFoundException {
        if (mostActivePlayer == null) {
            throw new PlayerNotFoundException();
        }
        return mostActivePlayer;
    }

    @Override
    public SportEvent bestSportEvent() throws SportEventNotFoundException {
        if (bestSportEvent.size() == 0) {
            throw new SportEventNotFoundException();
        }
        return bestSportEvent.elementAt(0);
    }

    @Override
    public void addRole(String roleId, String description) {
        Role role = new Role(roleId, description);
        for (int i = 0; i<=numroles; i++){
            if(roles[i]!=null){
                if(roles[i].getRoleId().equals(role.getRoleId())){
                    roles[i] = role;
                    break;
                }
            }
            else {
                roles[i] = role;
                numroles++;
                break;
            }
        }
    }

    @Override
    public void addWorker(String dni, String name, String surname, LocalDate birthDay, String roleId) {
        Worker worker = new Worker(dni, name, surname, birthDay, roleId);
        if(workers.containsKey(dni)){
            String rId = workers.get(dni).getRoleId();
            Role r = getRole(rId);
            workers.delete(dni);
            deleteRoleWorker(r, worker);
        }
        workers.put(dni, worker);
        Role role = getRole(roleId);
        role.addWorker(worker);
    }

    @Override
    public void assignWorker(String dni, String eventId) throws WorkerNotFoundException, WorkerAlreadyAssignedException, SportEventNotFoundException {
        SportEvent sportEvent = getSportEvent(eventId);
        if(sportEvent==null){
            throw new SportEventNotFoundException();
        }
        Worker worker = getWorker(dni);
        if(worker==null){
            throw new WorkerNotFoundException();
        }
        if(sportEvent.hasWorker(worker)){
            throw new WorkerAlreadyAssignedException();
        }
        sportEvent.addWorker(worker);
    }

    @Override
    public Iterator<Worker> getWorkersBySportEvent(String eventId) throws SportEventNotFoundException, NoWorkersException {
        SportEvent sportEvent = getSportEvent(eventId);
        if(sportEvent==null){
            throw new SportEventNotFoundException();
        }
        Iterator<Worker> it = sportEvent.getWorkers();
        if(!it.hasNext()){
            throw new NoWorkersException();
        }
        return it;
    }

    @Override
    public Iterator<Worker> getWorkersByRole(String roleId) throws NoWorkersException {
        Iterator<Worker> it = getRole(roleId).getWorkers();
        if(!it.hasNext()){
            throw new NoWorkersException();
        }
        return it;
    }

    @Override
    public Level getLevel(String playerId) throws PlayerNotFoundException {
        Player player = getPlayer(playerId);
        if(player==null){
            throw new PlayerNotFoundException();
        }
        return player.getLevel();
    }

    @Override
    public Iterator<Enrollment> getSubstitutes(String eventId) throws SportEventNotFoundException, NoSubstitutesException {
        SportEvent sportEvent = getSportEvent(eventId);
        if(sportEvent==null){
            throw new SportEventNotFoundException();
        }
        if(sportEvent.getNumSubstitutes()==0){
            throw new NoSubstitutesException();
        }
        return sportEvent.getSubstitutes();
    }

    @Override
    public void addAttender(String phone, String name, String eventId) throws AttenderAlreadyExistsException, SportEventNotFoundException, LimitExceededException {
        SportEvent sportEvent = getSportEvent(eventId);
        if(sportEvent == null){
            throw new SportEventNotFoundException();
        }
        Attender attender = sportEvent.getAttender(phone);
        if(attender!=null){
            throw new AttenderAlreadyExistsException();
        }
        if(sportEvent.numAttenders()+sportEvent.numPlayers()>=sportEvent.getMax()){
            throw new LimitExceededException();
        }
        sportEvent.addAttender(phone, name);
        updateBestSportEventByAttenders(sportEvent);

        OrganizingEntity organizingEntity = sportEvent.getOrganizingEntity();
        organizingEntity.addAttender();
    }

    @Override
    public Attender getAttender(String phone, String sportEventId) throws SportEventNotFoundException, AttenderNotFoundException {
        SportEvent sportEvent = getSportEvent(sportEventId);
        if(sportEvent==null){
            throw new SportEventNotFoundException();
        }
        Attender attender = sportEvent.getAttender(phone);
        if(attender==null){
            throw new AttenderNotFoundException();
        }
        return attender;
    }

    @Override
    public Iterator<Attender> getAttenders(String eventId) throws SportEventNotFoundException, NoAttendersException {
        SportEvent sportEvent = getSportEvent(eventId);
        if(sportEvent==null){
            throw new SportEventNotFoundException();
        }
        if(sportEvent.numAttenders()==0){
            throw new NoAttendersException();
        }
        return sportEvent.getAttenders();
    }

    @Override
    public Iterator<OrganizingEntity> best5OrganizingEntities() throws NoAttendersException {
        if (organizingEntities.isEmpty()) {
            throw new NoAttendersException();
        }
        updateBestOrganizingEntity();
        return best5OrganizingEntities.values();
    }

    @Override
    public SportEvent bestSportEventByAttenders() throws NoSportEventsException {
        if (bestSportEventByAttenders == null) {
            throw new NoSportEventsException();
        }
        return bestSportEventByAttenders;
    }

    @Override
    public void addFollower(String playerId, String playerFollowerId) throws PlayerNotFoundException {

    }

    @Override
    public Iterator<Player> getFollowers(String playerId) throws PlayerNotFoundException, NoFollowersException {
        return null;
    }

    @Override
    public Iterator<Player> getFollowings(String playerId) throws PlayerNotFoundException, NoFollowingException {
        return null;
    }

    @Override
    public Iterator<Player> recommendations(String playerId) throws PlayerNotFoundException, NoFollowersException {
        return null;
    }

    @Override
    public Iterator<Post> getPosts(String playerId) throws PlayerNotFoundException, NoPostsException {
        return null;
    }

    @Override
    public int numPlayers() {
        return players.size();
    }

    @Override
    public int numOrganizingEntities() {
        return organizingEntities.size();
    }

    @Override
    public int numFiles() {
        return totalFiles;
    }

    @Override
    public int numRejectedFiles() {
        return rejectedFiles;
    }

    @Override
    public int numPendingFiles() {
        return files.size();
    }

    @Override
    public int numSportEvents() {
        return sportEvents.size();
    }

    @Override
    public int numSportEventsByPlayer(String playerId) {
        Player player = getPlayer(playerId);
        return (player!=null?player.numEvents():0);
    }

    @Override
    public int numPlayersBySportEvent(String sportEventId) {
        SportEvent sportEvent = getSportEvent(sportEventId);
        return (sportEvent!=null?sportEvent.numPlayers(): 0);
    }

    @Override
    public int numSportEventsByOrganizingEntity(String orgId) {
        OrganizingEntity organization =  getOrganizingEntity(orgId);
        return (organization!=null? organization.numEvents():0);
    }

    @Override
    public int numSubstitutesBySportEvent(String sportEventId) {
        SportEvent sportEvent = getSportEvent(sportEventId);

        return (sportEvent!=null?sportEvent.getNumSubstitutes():0);
    }

    @Override
    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    @Override
    public SportEvent getSportEvent(String eventId) {
        return sportEvents.get(eventId);
    }

    @Override
    public OrganizingEntity getOrganizingEntity(String id) {
        return organizingEntities.get(id);
    }

    @Override
    public File currentFile() {
        return (files.size() > 0 ? files.peek() : null);
    }

    @Override
    public int numRoles() {
        return numroles;
    }

    @Override
    public Role getRole(String roleId) {
        for(int i=0; i<=numroles ;i++){
            if(roles[i].getRoleId().equals(roleId)){
                return roles[i];
            }
        }
        return null;
    }

    @Override
    public int numWorkers() {
        return workers.size();
    }

    @Override
    public Worker getWorker(String dni) {
        Worker worker = null;
        if(workers.containsKey(dni)){
            worker = workers.get(dni);
        }
        return worker;
    }

    @Override
    public int numWorkersByRole(String roleId) {
        return getRole(roleId).getNumWorkers();
    }

    @Override
    public int numWorkersBySportEvent(String sportEventId) {
        SportEvent sportEvent = getSportEvent(sportEventId);
        return sportEvent.numWorkers();
    }

    @Override
    public int numRatings(String playerId) {
        Player player = getPlayer(playerId);
        return player.getNumRatings();
    }

    @Override
    public int numAttenders(String sportEventId) {
        int numAttenders = 0;
        SportEvent sportEvent = getSportEvent(sportEventId);
        if(sportEvent!=null){
            numAttenders = sportEvent.numAttenders();
        }
        return numAttenders;
    }

    @Override
    public int numFollowers(String playerId) {
        return 0;
    }

    @Override
    public int numFollowings(String playerId) {
        return 0;
    }

    private void updateMostActivePlayer(Player player) {
        if (mostActivePlayer == null) {
            mostActivePlayer = player;
        }
        else if (player.numSportEvents() > mostActivePlayer.numSportEvents()) {
            mostActivePlayer = player;
        }
    }

    private void updateBestSportEvent(SportEvent sportEvent) {
        bestSportEvent.delete(sportEvent);
        bestSportEvent.update(sportEvent);
    }

    private void deleteRoleWorker(Role role, Worker worker){
        if(role.containsWorker(worker)){
            role.deleteWorker(worker);
        }
    }

    private void updateBestSportEventByAttenders(SportEvent sportEvent) {
        if (bestSportEventByAttenders == null) {
            bestSportEventByAttenders = sportEvent;
        }
        else if (sportEvent.numAttenders() > bestSportEventByAttenders.numAttenders()) {
            bestSportEventByAttenders = sportEvent;
        }
    }

    private void updateBestOrganizingEntity(){
        Iterator<OrganizingEntity> iterator = organizingEntities.values();
        while (iterator.hasNext()) {
            best5OrganizingEntities.update(iterator.next());
        }
    }

}
