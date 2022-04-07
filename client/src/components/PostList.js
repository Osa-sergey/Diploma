import React from 'react';
import PostItem from "./PostItem";
import postItem from "./PostItem";
import IPost from "../interfaces/IPost";

const PostList = ({postList, name }: any) => {
    return (
        <div className="post_list">
            <div className="post_list_header">
                <h1>Список оптимизаций пользователя {name}</h1>
            </div>
            <button className="post_list_add_btn">Add post</button>
            {
                postList.map((post: IPost) =>
                    <PostItem post={post.post} key={post.post.id} remove={postItem}/>
                )
            }
        </div>
    );
};

export default PostList;