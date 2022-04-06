import React, {useState} from "react";
import './styles/App.css';
import PostList from "./components/PostList";

function App() {
    const [posts, setPosts] = useState([
        {post:{id: 1, title: "Первый пост", body: "пока что просто текст поста"}},
        {post:{id: 2, title: "Первый пост", body: "пока что просто текст поста"}},
        {post:{id: 1, title: "Первый пост", body: "пока что просто текст поста"}},
        {post:{id: 1, title: "Первый пост", body: "пока что просто текст поста"}},
        {post:{id: 1, title: "Первый пост", body: "пока что просто текст поста"}},
        {post:{id: 1, title: "Первый пост", body: "пока что просто текст поста"}},
        {post:{id: 1, title: "Первый пост", body: "пока что просто текст поста"}}
    ])
  return (
    <div className="app">
        <PostList postList={posts} name={"serg"}/>
        <div className="viewer">hello</div>
    </div>
  );
}

export default App;
