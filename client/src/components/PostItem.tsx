import React from 'react';
import IPost from "../interfaces/IPost";

const PostItem = (item: IPost) => {
    return (
        <div className="post">
            <div className="post_content">
                <h1>
                    <strong>{item.post.title}</strong>
                </h1>
                <div>
                    <h3>{item.post.body}</h3>
                </div>
            </div>
            <div className="post_button">
                <button>Delete</button>
            </div>
        </div>
    );
};

export default PostItem;