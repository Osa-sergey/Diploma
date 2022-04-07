import React from 'react';
import IPost from "../interfaces/IPost";

const PostItem = (item: IPost) => {
    return (
        <div className="post">
            <div className="post_content">
                <h1>
                    <strong className="header_text">{item.post.title}</strong>
                </h1>
                <div>
                    <h3>{item.post.body}</h3>
                </div>
            </div>
            <button className="post_button">Delete</button>
        </div>
    );
};

export default PostItem;