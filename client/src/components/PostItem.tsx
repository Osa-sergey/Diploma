import React from 'react';
import IPost from "../interfaces/IPost";
import AppButton from "./UI/button/AppButton";

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
            <div className="post_button">
                <AppButton>Delete</AppButton>
            </div>
        </div>
    );
};

export default PostItem;