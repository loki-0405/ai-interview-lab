import React, { useState } from 'react';
import api from './api';
import './App.css';

const ChatBox = () => {
  const [file, setFile] = useState(null);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [message, setMessage] = useState('');
  const [chat, setChat] = useState([]);
  const [started, setStarted] = useState(false);
  const [loading, setLoading] = useState(false); // ⬅ loading state

  const handleStart = async () => {
    if (!file || !title || !description) {
      alert("Please fill all fields and upload a file.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);
    formData.append("title", title);
    formData.append("description", description);

    try {
      const res = await api.post("http://localhost:8088/api/prep/start", formData);
      setStarted(true);
      alert("Session started successfully!");
    } catch (err) {
      console.error(err);
      alert("Failed to start session.");
    }
  };

  const sendMessage = async () => {
    if (!message.trim()) return;

    setLoading(true); // ⬅ start loading

    try {
      const res = await api.post("http://localhost:8088/api/prep", message, {
        headers: { "Content-Type": "text/plain" }
      });
      setChat(res.data);
      setMessage('');
    } catch (err) {
      console.error(err);
      alert("Error sending message.");
    } finally {
      setLoading(false); // ⬅ stop loading
    }
  };

  return (
    <div className="chat-container">
      {!started ? (
        <>
          <h2>Start Interview Session</h2>
          <input type="file" onChange={e => setFile(e.target.files[0])} />
          <input type="text" placeholder="Job Title" value={title} onChange={e => setTitle(e.target.value)} />
          <textarea rows={4} placeholder="Job Description" value={description} onChange={e => setDescription(e.target.value)} />
          <button onClick={handleStart}>Start</button>
        </>
      ) : (
        <>
          <h2>Interview Chat</h2>

          <div className="chat-history">
            {chat.map((msg, idx) => (
              <div key={idx} className={`message ${msg.sender}`}>
                <div className="bubble">
                  <strong>{msg.sender === 'user' ? 'You' : 'AI'}:</strong> {msg.content}
                </div>
              </div>
            ))}
            {loading && (
              <div className="loading-spinner">
                <span className="loader"></span> AI is thinking...
              </div>
            )}
          </div>

          <div className="chat-input">
            <input
              type="text"
              value={message}
              onChange={e => setMessage(e.target.value)}
              placeholder="Type your message..."
              onKeyDown={e => e.key === 'Enter' && sendMessage()}
              disabled={loading}
            />
            <button onClick={sendMessage} disabled={loading}>
              {loading ? 'Sending...' : 'Send'}
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default ChatBox;
