import React from 'react';
import IPost from "../interfaces/IPost";

const PostItem = (item: IPost) => {
    return (
        <div className="post">
            <div className="post_content">
                <h1>
                    <strong className="header_text">{item.post.title}</strong>
                </h1>
                <h3><b>Дата оптимизации:</b> {item.post.date}</h3>
                <h3><b>Статус:</b> {item.post.status}</h3>
            </div>
            <button className="post_button">Delete</button>
        </div>
    );
};

export default PostItem;