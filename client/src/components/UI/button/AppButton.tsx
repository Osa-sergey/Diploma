import React from 'react';
import styles from './AppButton.module.css';

const AppButton = ({children, ...props}:any) => {
    return (
        <button {...props} className={styles.appBtn}>
            {children}
        </button>
    );
};

export default AppButton;