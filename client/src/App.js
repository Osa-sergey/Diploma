import React, {useState} from "react";
import './styles/App.css';
import PostList from "./components/PostList";
import IPost from "./interfaces/IPost";

function App() {
    const [posts, setPosts] = useState([
        {post:{id: 1, title: "Первый пост", date: "10.04.2000", status: "В процессе"}},
        {post:{id: 2, title: "Первый пост", date: "10.04.2000", status: "В процессе"}},
        {post:{id: 3, title: "Первый пост2", date: "10.04.2000", status: "В процессе"}},
        {post:{id: 4, title: "Первый пост", date: "10.04.2000", status: "В процессе"}},
        {post:{id: 5, title: "Первый пост", date: "10.04.2000", status: "В процессе"}},
        {post:{id: 6, title: "Первый пост", date: "10.04.2000", status: "В процессе"}}
    ])

    const removePost = (post: IPost): void => {
        setPosts(posts.filter(p => p.post.id !== post.post.id))
    }

  return (
    <div className="app">
        <PostList postList={posts} name={"serg"} removePost={removePost}/>
        <div className="viewer">hello</div>
    </div>
  );
}

export default App;
