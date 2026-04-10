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
