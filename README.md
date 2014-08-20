nogc
====

An architecture of nogc fo jvm.

Inroduction:
This architecture takes ByteBuffer's direcrAllocate api to allocate memory out of heap and manage this kind of memory without worrying about JVM GC'problem. An objet can be serialized into byte values in CSON format which is like BSON, but more effective. The data which is serialized from an object is stroed on the memory out of heap and be indexed with a long value. This architecture divides the memory into segments with the idea like ConcurrentHashMap. The objects' data are distributed into diffirent segments based on object's hash id, which is the index(long value).
