DS - PR2

Este documento contiene información adicional de la entrega de la práctica 2 de la asignatura Diseño de estructuras de
datos impartido en el Grado en Ingeniería Informática de la UOC.

- Autor: Héctor Mario Medina Cabanelas
- Usuario: hmedinac
- Email: hmedinac@uoc.edu

# Antecedentes.

Esta práctica ha sido desarrollada en base a la solución de la Prueba de Evaluación Continua número 2 (PEC2) y la 
solución de la Práctica 1 (PR1). En ella se especifican ciertas estructuras de datos para un caso de uso concreto. A 
continuación se detallan las estructuras de datos  implementadas y las desarrolladas.

# Clase SportEvent4ClubImpl.

Esta clase encapsula toda la funcionalidad de la práctica. En concreto, debemos crear los siguientes atributos privados:

- Player mostActivePlayer: Apuntador al jugador más activo.
- Dictionary<String, SportEvent> sportEvents: Diccionario clave-valor de eventos deportivos, donde la clave es el ID.
- OrderedVector<SportEvent> bestSportEvent: Vector ordenado de eventos deportivos. 
- Dictionary<String, Player> players: Diccionario clave-valor de jugadores.
- HashTable<String,OrganizingEntity> organizingEntities: Tabla Hash de entidades organizativas, la clave es el ID.
- PriorityQueue<File> files: Cola de prioridad de fichas.
- int totalFiles: Total de fichas aprobadas.
- int rejectedFiles: Total de fichas rechazadas.
- Role[] roles: Array de roles.
- int numroles: Número de roles. Esto es debido a la naturaleza del vector de roles, es más sencillo conocer el número de
elementos que contiene almacenando un entero de forma paralela ya que roles.length nos devuelve la longitud del vector.
- HashTable<String, Worker> workers: Tabla hash de trabajadores donde la clave es el DNI.
- SportEvent bestSportEventByAttenders: Apuntador a evento deportivo más concurrido.
- OrderedVector<OrganizingEntity> best5OrganizingEntities: Vector ordenado que contiene las 5 mejores entidades.

# Clases adicionales.

No ha sido necesario crear clases adicionales a las requeridas.

# Métodos adicionales.

En la clase SportEvent4ClubImpl se han desarrollado 5 métodos auxiliares para organizar el código más fácilmente y que sea
más legible y fácil de mantener. Estos métodos son privados, ya que es necesario que sean accedidos desde fuera de la clase.

# Comparadores.

Para realizar el ordenamiento de varias estructuras de datos se han utilizado comparadores. Los comparadores utilizan el 
método `compareTo()` y requiren la interface `Comparable<>`. Es el caso de las colas de prioridad y los vectores ordenados.

# Comentarios finales.

La práctica ha sido muy interesante para conocer el funcionamiento de estructuras de datos complejas y su implementación
en Java. Además, se ha comprobado que la metodología TDD (Test Driven Development) es una metodología de desarrollo que
proporciona estructuras de datos sólidas que responden a las especificaciones deseadas en casos límites que de otro modo
quedarían indeterminados.