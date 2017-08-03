package testing

import spark.Spark.*
import java.util.concurrent.atomic.AtomicInteger
import com.fasterxml.jackson.databind.ObjectMapper
import org.omg.CORBA.Object

data class User(val name: String, val email: String, val age: Int, val id: Int)

class UserDAO {
    val users = hashMapOf(
            0 to User("Ewan", "ewan@ladbible.com", 28, 1),
            1 to User("Amelia", "t@t.com", 24, 2),
            2 to User("Alice", "lol@lol.com",25, 3)
    )

    var lastId: AtomicInteger = AtomicInteger(users.size - 1)

    fun save(name: String, email: String, age: Int) {
        val id = lastId.incrementAndGet()
        users.put(id, User(name, email, age, id))
    }

    fun findById(id: Int): User? {
       return users[id]
    }

    fun findByEmail(email: String): User? {
        return users.values.find { it.email == email }
    }

    fun update(id: Int, name: String, email: String, age: Int) {
        users.put(id, User(name, email, age, id))
    }

    fun delete(id: Int) {
        users.remove(id)
    }
}

val usersDao = UserDAO()

fun main(args: Array<String>) {
    println("Shit man...")

    var objectMapper = ObjectMapper()

    get("/hello") { req, res -> "You twat!" }

    path("/users") {
        get("") { req, res ->
            objectMapper.writeValueAsString(usersDao.users)
        }

        get("/:id") { req, res ->
            usersDao.findById(req.params("id").toInt())
        }

        get("/email/:email") { req, res ->
            usersDao.findByEmail(req.params("email"))
        }

        post("") { req, res ->
            println(req.body())
            val data = objectMapper.readValue(req.body(), User::class.java)
            usersDao.save(data.name, data.email, data.age)
            res.status(201)
            "okies"
        }

        patch("/:id") { req, res ->
            val data = objectMapper.readValue(req.body(), User::class.java)
            usersDao.update(req.params("id").toInt(), data.name, data.email, data.age)
            res.status(204)
            "okies"
        }

        delete("/:id") { req, res ->
            usersDao.delete(req.params("id").toInt())
            res.status(204)
            "okies"
        }
    }
}