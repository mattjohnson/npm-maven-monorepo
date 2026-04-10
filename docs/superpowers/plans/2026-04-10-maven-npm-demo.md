# Maven + NPM Demo Todo App Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create a working Todo web app with Spring Boot 2.7.18 backend (Maven) and React 17 frontend (NPM), with intentionally outdated dependencies for upgrade testing.

**Architecture:** Flat repo root with both `pom.xml` and `package.json`. Spring Boot REST API serves CRUD endpoints at `/api/todos` backed by H2 in-memory DB. React 17 frontend in `frontend/` uses Vite 3 dev server with proxy to Spring Boot.

**Tech Stack:** Java 17, Spring Boot 2.7.18, H2 2.1.214, Lombok 1.18.24, React 17.0.2, Axios 0.27.2, Vite 3.2.7

---

## File Map

| File | Responsibility |
|------|---------------|
| `pom.xml` | Maven build config, Spring Boot 2.7.18, all Java dependencies |
| `package.json` | NPM metadata, frontend scripts, React/Axios/Vite dependencies |
| `.gitignore` | Ignore build artifacts for both Maven and NPM |
| `src/main/java/com/example/todo/TodoApplication.java` | Spring Boot entry point |
| `src/main/java/com/example/todo/Todo.java` | JPA entity: id, title, completed, createdAt |
| `src/main/java/com/example/todo/TodoRepository.java` | Spring Data JPA repository interface |
| `src/main/java/com/example/todo/TodoController.java` | REST controller: CRUD on `/api/todos` |
| `src/main/resources/application.properties` | H2 config, Hibernate DDL, server port |
| `src/test/java/com/example/todo/TodoApplicationTests.java` | Spring Boot context load test |
| `src/test/java/com/example/todo/TodoControllerTests.java` | REST endpoint integration tests |
| `frontend/index.html` | HTML entry point for Vite |
| `frontend/vite.config.js` | Vite config with `/api` proxy to port 8080 |
| `frontend/src/main.jsx` | React 17 entry: ReactDOM.render |
| `frontend/src/App.jsx` | Todo list UI: add, toggle, delete todos via axios |
| `frontend/src/App.css` | Basic styling |

---

### Task 1: Project scaffolding — .gitignore, pom.xml, package.json

**Files:**
- Create: `.gitignore`
- Create: `pom.xml`
- Create: `package.json`

- [ ] **Step 1: Create .gitignore**

```gitignore
# Maven
target/

# NPM
node_modules/

# IDE
.idea/
*.iml
.vscode/
.classpath
.project
.settings/

# OS
.DS_Store
```

- [ ] **Step 2: Create pom.xml with Spring Boot 2.7.18**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>todo-app</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <name>todo-app</name>
    <description>Demo Todo App with Spring Boot and React</description>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.1.214</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.24</version>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 3: Create package.json with outdated dependencies**

```json
{
  "name": "todo-app-frontend",
  "version": "0.1.0",
  "private": true,
  "description": "Demo Todo App - React Frontend",
  "scripts": {
    "dev": "vite --config frontend/vite.config.js",
    "build": "vite build --config frontend/vite.config.js",
    "preview": "vite preview --config frontend/vite.config.js"
  },
  "dependencies": {
    "react": "17.0.2",
    "react-dom": "17.0.2",
    "axios": "0.27.2"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "2.2.0",
    "vite": "3.2.7"
  }
}
```

- [ ] **Step 4: Commit scaffolding**

```bash
git add .gitignore pom.xml package.json
git commit -m "feat: add project scaffolding with Maven and NPM build files"
```

---

### Task 2: Spring Boot application and entity

**Files:**
- Create: `src/main/java/com/example/todo/TodoApplication.java`
- Create: `src/main/java/com/example/todo/Todo.java`
- Create: `src/main/resources/application.properties`

- [ ] **Step 1: Create application.properties**

```properties
spring.datasource.url=jdbc:h2:mem:tododb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
server.port=8080
```

- [ ] **Step 2: Create TodoApplication.java**

```java
package com.example.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }
}
```

- [ ] **Step 3: Create Todo.java entity**

```java
package com.example.todo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private boolean completed;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/todo/TodoApplication.java \
       src/main/java/com/example/todo/Todo.java \
       src/main/resources/application.properties
git commit -m "feat: add Spring Boot application entry point and Todo entity"
```

---

### Task 3: Repository and REST controller

**Files:**
- Create: `src/main/java/com/example/todo/TodoRepository.java`
- Create: `src/main/java/com/example/todo/TodoController.java`

- [ ] **Step 1: Create TodoRepository.java**

```java
package com.example.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
```

- [ ] **Step 2: Create TodoController.java**

```java
package com.example.todo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "http://localhost:5173")
public class TodoController {

    private final TodoRepository repository;

    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Todo> getAllTodos() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        todo.setId(null);
        todo.setCompleted(false);
        return repository.save(todo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails) {
        return repository.findById(id)
                .map(todo -> {
                    todo.setTitle(todoDetails.getTitle());
                    todo.setCompleted(todoDetails.isCompleted());
                    return ResponseEntity.ok(repository.save(todo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        return repository.findById(id)
                .map(todo -> {
                    repository.delete(todo);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/example/todo/TodoRepository.java \
       src/main/java/com/example/todo/TodoController.java
git commit -m "feat: add TodoRepository and REST controller with CRUD endpoints"
```

---

### Task 4: Backend tests

**Files:**
- Create: `src/test/java/com/example/todo/TodoApplicationTests.java`
- Create: `src/test/java/com/example/todo/TodoControllerTests.java`

- [ ] **Step 1: Create TodoApplicationTests.java**

```java
package com.example.todo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TodoApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 2: Create TodoControllerTests.java**

```java
package com.example.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void getAllTodos_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createTodo_returnsSavedTodo() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Test todo");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test todo")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void getTodoById_returnsNotFoundForMissing() throws Exception {
        mockMvc.perform(get("/api/todos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTodo_togglesCompleted() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Toggle me");
        Todo saved = repository.save(todo);

        saved.setCompleted(true);

        mockMvc.perform(put("/api/todos/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void deleteTodo_removesTodo() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Delete me");
        Todo saved = repository.save(todo);

        mockMvc.perform(delete("/api/todos/" + saved.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/todos/" + saved.getId()))
                .andExpect(status().isNotFound());
    }
}
```

- [ ] **Step 3: Run tests to verify they pass**

Run: `mvn test`
Expected: All 5 tests pass (contextLoads, getAllTodos_returnsEmptyList, createTodo_returnsSavedTodo, getTodoById_returnsNotFoundForMissing, updateTodo_togglesCompleted, deleteTodo_removesTodo)

- [ ] **Step 4: Commit**

```bash
git add src/test/java/com/example/todo/TodoApplicationTests.java \
       src/test/java/com/example/todo/TodoControllerTests.java
git commit -m "test: add Spring Boot context and REST controller integration tests"
```

---

### Task 5: React frontend

**Files:**
- Create: `frontend/index.html`
- Create: `frontend/vite.config.js`
- Create: `frontend/src/main.jsx`
- Create: `frontend/src/App.jsx`
- Create: `frontend/src/App.css`

- [ ] **Step 1: Create frontend/index.html**

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Todo App</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.jsx"></script>
  </body>
</html>
```

- [ ] **Step 2: Create frontend/vite.config.js**

```js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  root: 'frontend',
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

- [ ] **Step 3: Create frontend/src/main.jsx**

Note: Uses `ReactDOM.render()` (React 17 API), not `createRoot()` (React 18+).

```jsx
import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import './App.css';

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);
```

- [ ] **Step 4: Create frontend/src/App.jsx**

```jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

function App() {
  const [todos, setTodos] = useState([]);
  const [newTitle, setNewTitle] = useState('');

  useEffect(() => {
    fetchTodos();
  }, []);

  const fetchTodos = async () => {
    const response = await axios.get('/api/todos');
    setTodos(response.data);
  };

  const addTodo = async (e) => {
    e.preventDefault();
    if (!newTitle.trim()) return;
    await axios.post('/api/todos', { title: newTitle });
    setNewTitle('');
    fetchTodos();
  };

  const toggleTodo = async (todo) => {
    await axios.put(`/api/todos/${todo.id}`, {
      ...todo,
      completed: !todo.completed,
    });
    fetchTodos();
  };

  const deleteTodo = async (id) => {
    await axios.delete(`/api/todos/${id}`);
    fetchTodos();
  };

  return (
    <div className="app">
      <h1>Todo App</h1>
      <form onSubmit={addTodo} className="add-form">
        <input
          type="text"
          value={newTitle}
          onChange={(e) => setNewTitle(e.target.value)}
          placeholder="What needs to be done?"
        />
        <button type="submit">Add</button>
      </form>
      <ul className="todo-list">
        {todos.map((todo) => (
          <li key={todo.id} className={todo.completed ? 'completed' : ''}>
            <span onClick={() => toggleTodo(todo)}>{todo.title}</span>
            <button onClick={() => deleteTodo(todo.id)} className="delete-btn">
              Delete
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;
```

- [ ] **Step 5: Create frontend/src/App.css**

```css
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background-color: #f5f5f5;
  color: #333;
}

.app {
  max-width: 600px;
  margin: 40px auto;
  padding: 20px;
}

h1 {
  text-align: center;
  margin-bottom: 20px;
  color: #1a1a1a;
}

.add-form {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.add-form input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.add-form button {
  padding: 10px 20px;
  background-color: #4a90d9;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

.add-form button:hover {
  background-color: #357abd;
}

.todo-list {
  list-style: none;
}

.todo-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: white;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 8px;
  cursor: pointer;
}

.todo-list li.completed span {
  text-decoration: line-through;
  color: #999;
}

.delete-btn {
  padding: 4px 12px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.delete-btn:hover {
  background-color: #c0392b;
}
```

- [ ] **Step 6: Commit frontend**

```bash
git add frontend/
git commit -m "feat: add React 17 frontend with todo list UI"
```

---

### Task 6: Generate package-lock.json and final verification

**Files:**
- Create: `package-lock.json` (generated)

- [ ] **Step 1: Install NPM dependencies to generate package-lock.json**

Run: `npm install`
Expected: `node_modules/` created, `package-lock.json` generated in repo root.

- [ ] **Step 2: Verify package-lock.json exists and has content**

Run: `head -20 package-lock.json`
Expected: JSON with lockfileVersion, dependencies for react, react-dom, axios, vite, @vitejs/plugin-react.

- [ ] **Step 3: Run Maven tests to verify backend still works**

Run: `mvn test`
Expected: All tests pass.

- [ ] **Step 4: Commit package-lock.json**

```bash
git add package-lock.json
git commit -m "chore: add package-lock.json with pinned dependency versions"
```

---

### Task 7: Final cleanup and verification commit

- [ ] **Step 1: Verify project structure**

Run: `find . -not -path './node_modules/*' -not -path './target/*' -not -path './.git/*' -type f | sort`

Expected output should include:
```
./.gitignore
./docs/superpowers/plans/2026-04-10-maven-npm-demo.md
./docs/superpowers/specs/2026-04-10-maven-npm-demo-design.md
./frontend/index.html
./frontend/src/App.css
./frontend/src/App.jsx
./frontend/src/main.jsx
./frontend/vite.config.js
./package-lock.json
./package.json
./pom.xml
./src/main/java/com/example/todo/Todo.java
./src/main/java/com/example/todo/TodoApplication.java
./src/main/java/com/example/todo/TodoController.java
./src/main/java/com/example/todo/TodoRepository.java
./src/main/resources/application.properties
./src/test/java/com/example/todo/TodoApplicationTests.java
./src/test/java/com/example/todo/TodoControllerTests.java
```

- [ ] **Step 2: Verify all three root build files exist**

Run: `ls -la pom.xml package.json package-lock.json`
Expected: All three files present in the repo root.

- [ ] **Step 3: Run full Maven test suite one final time**

Run: `mvn clean test`
Expected: BUILD SUCCESS, all tests pass.
