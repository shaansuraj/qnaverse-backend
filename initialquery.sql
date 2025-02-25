
--------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,        -- Hashed password
    bio TEXT DEFAULT NULL,
    profile_picture VARCHAR(255) DEFAULT NULL, --path to image
    instagram_url TEXT DEFAULT NULL,
    github_url TEXT DEFAULT NULL,
    linkedin_url TEXT DEFAULT NULL,
    role ENUM('user', 'admin') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--------------------------------------------------------------------------------
-- 3) questions table
--    Stores questions posted by users.
--------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    media VARCHAR(255) DEFAULT NULL,         -- Path of images/videos 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    likes INT DEFAULT 0,
    tags JSON,                               
    approved BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
-- 4) answers table
--    Stores answers to questions.
--------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS answers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    upvotes INT DEFAULT 0,
    downvotes INT DEFAULT 0,

    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
-- 5) notifications table
--    Stores notifications for users (e.g. new answers, likes, etc.).
--------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,                         -- The recipient user
    type ENUM('new_answer','like','follow','mention','upvote'),
    reference_id INT,                             -- Could refer to a question, answer, or user
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_status BOOLEAN DEFAULT FALSE,            -- false=unread, true=read

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
-- 6) question_tags table
--    Links questions to tags (if not using separate tags table).
--------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS question_tags (
    question_id INT NOT NULL,
    tag_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (question_id, tag_name),

    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
-- 7) reports table
--    Stores user reports for admin review (reporting Q/A).
--------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    question_id INT NULL,
    answer_id INT NULL,
    reason ENUM('Sensitive','Mature','Self-harm','Violence'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (answer_id) REFERENCES answers(id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
-- 8) follows table
--    Stores user follow relationships.
--------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS follows (
    follower_id INT NOT NULL,
    following_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
-- 9) saved_questions table
--    Tracks which questions are saved by which users.
--------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS saved_questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    question_id INT NOT NULL,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
-- 10) likes table
--     Tracks which users liked which questions.
--------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS likes (
    user_id INT NOT NULL,
    question_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, question_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
-- 11) answer_votes table
--     Tracks upvotes & downvotes for answers.
--------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS answer_votes (
    user_id INT NOT NULL,
    answer_id INT NOT NULL,
    vote_type ENUM('upvote','downvote') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, answer_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (answer_id) REFERENCES answers(id) ON DELETE CASCADE
);

--------------------------------------------------------------------------------
-- 12) blocked_users table
--     Stores blocked user relationships (user blocks other).
--------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS blocked_users (
    blocker_id INT NOT NULL,
    blocked_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (blocker_id, blocked_id),
    FOREIGN KEY (blocker_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_id) REFERENCES users(id) ON DELETE CASCADE
);
