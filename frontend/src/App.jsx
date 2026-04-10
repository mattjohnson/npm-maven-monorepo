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
