--適当なサンプルデータ投入

INSERT INTO `users` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `display_name`, `enabled`, `profile_image_url`, `self_introduction`, `subject`)
VALUES
	(2, '2024-09-27 06:08:34.236923', 'System', '2024-10-04 05:24:36.663805', '2', 'TEPPEI', 1, 'f4fb1677-2754-45d1-ab63-ead13f8463ed_blob', NULL, '110049863947646140297'),
	(3, '2024-10-05 12:39:49.000000', 'System', '2024-10-05 12:39:49.000000', 'System', 'John Doe', 1, 'a3e63d8d-c811-4264-8cea-bb6af370b636_blob', 'Hello, I am John. I love blogging and sharing ideas.', '1234567890'),
	(4, '2024-10-05 12:40:20.000000', 'System', '2024-10-05 12:40:20.000000', 'System', 'Mike', 1, 'a3e63d8d-c811-4264-8cea-bb6af370b636_blob', 'Hello, I am Mike. I love blogging and sharing ideas.', '1234567890');

INSERT INTO `artists` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `image_url`, `name`)
VALUES
	('06sqnZl2EzpyIamhM1u3eO', '2024-10-05 09:12:32.950046', '2', '2024-10-05 09:12:32.950046', '2', 'https://i.scdn.co/image/ab6761610000e5eb83bf1aaf288cee00e275124e', 'w.o.d.'),
	('0bOGtsbotnQiTIHQMehEZx', '2024-10-05 09:12:32.960645', '2', '2024-10-05 09:12:32.960645', '2', 'https://i.scdn.co/image/ab6761610000e5eb5dd0ed57e6cac877a3bf4307', 'DNA GAINZ'),
	('0hOOhY33adbUdYjrzJHYlX', '2024-10-05 09:12:32.937264', '2', '2024-10-05 09:12:32.937264', '2', 'https://i.scdn.co/image/ab6761610000e5eb41296c1902943301b47b9158', 'SIX LOUNGE'),
	('3cbd5GWGOknxmFAe77MDbk', '2024-10-05 09:12:32.953645', '2', '2024-10-05 09:12:32.953645', '2', 'https://i.scdn.co/image/ab6761610000e5eb74eee2af6bd02df43e9595cc', 'ELLEGARDEN'),
	('7k73EtZwoPs516ZxE72KsO', '2024-10-05 09:12:32.957124', '2', '2024-10-05 09:12:32.957124', '2', 'https://i.scdn.co/image/ab6761610000e5ebfe5a99c098f41a6b93f0174b', 'ONE OK ROCK'),
	('7kOS7xo3ryc1MmhfP0fNnX', '2024-10-04 05:24:36.792616', '2', '2024-10-04 05:24:36.792616', 'System', 'https://i.scdn.co/image/ab6761610000e5ebb6ac36d30265bf0eb76615d4', 'TENDOUJI');

INSERT INTO `user_artist` (`created_at`, `created_by`, `updated_at`, `updated_by`, `artist_id`, `user_id`)
VALUES
	('2024-10-05 09:12:32.950915', '2', '2024-10-05 09:12:32.950915', '2', '06sqnZl2EzpyIamhM1u3eO', 2),
	('2024-10-05 09:12:32.950915', 'System', '2024-10-05 09:12:32.950915', 'System', '06sqnZl2EzpyIamhM1u3eO', 3),
	('2024-10-05 09:12:32.961404', '2', '2024-10-05 09:12:32.961404', '2', '0bOGtsbotnQiTIHQMehEZx', 2),
	('2024-10-05 09:12:32.961404', 'System', '2024-10-05 09:12:32.961404', 'System', '0bOGtsbotnQiTIHQMehEZx', 3),
	('2024-10-05 09:12:32.945411', '2', '2024-10-05 09:12:32.945411', '2', '0hOOhY33adbUdYjrzJHYlX', 2),
	('2024-10-05 09:12:32.945411', 'System', '2024-10-05 09:12:32.945411', 'System', '0hOOhY33adbUdYjrzJHYlX', 3),
	('2024-10-05 09:12:32.954417', '2', '2024-10-05 09:12:32.954417', '2', '3cbd5GWGOknxmFAe77MDbk', 2),
	('2024-10-05 09:12:32.954417', 'System', '2024-10-05 09:12:32.954417', 'System', '3cbd5GWGOknxmFAe77MDbk', 4),
	('2024-10-05 09:12:32.957951', '2', '2024-10-05 09:12:32.957951', '2', '7k73EtZwoPs516ZxE72KsO', 2),
	('2024-10-05 09:12:32.957951', 'System', '2024-10-05 09:12:32.957951', 'System', '7k73EtZwoPs516ZxE72KsO', 4),
	('2024-10-04 05:24:36.829113', '2', '2024-10-04 05:24:36.829113', '2', '7kOS7xo3ryc1MmhfP0fNnX', 2),
	('2024-10-04 05:24:36.829113', 'System', '2024-10-04 05:24:36.829113', 'System', '7kOS7xo3ryc1MmhfP0fNnX', 4);


INSERT INTO `follows` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `follow_at`, `followed_id`, `follower_id`)
VALUES
	(1, '2024-10-05 13:00:49.000000', 'System', '2024-10-05 13:00:49.000000', 'System', '2024-10-05 13:00:49.000000', 3, 2),
	(2, '2024-10-05 13:01:11.000000', 'System', '2024-10-05 13:01:11.000000', 'System', '2024-10-05 13:01:11.000000', 4, 3),
	(4, '2024-10-05 13:01:21.000000', 'System', '2024-10-05 13:01:21.000000', 'System', '2024-10-05 13:01:21.000000', 3, 4),
	(5, '2024-10-05 13:20:18.000000', 'System', '2024-10-05 13:20:18.000000', 'System', '2024-10-05 13:20:18.000000', 2, 4),
	(7, '2024-10-05 13:20:25.000000', 'System', '2024-10-05 13:20:25.000000', 'System', '2024-10-05 13:20:25.000000', 2, 3);

INSERT INTO `blogs` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `blog_created_time`, `blog_updated_time`, `category`, `comment_count`, `content`, `is_deleted`, `like_count`, `setlist`, `slug`, `status`, `tags`, `thumbnail_url`, `title`, `view_count`, `author_id`)
VALUES
	(1, '2024-10-05 12:32:50.000000', '2', '2024-10-05 12:32:50.000000', '2', '2024-10-05 12:32:50.000000', '2024-10-05 12:32:50.000000', 'REPORT', 0, '{"type": "doc", "content": [{"type": "paragraph", "content": [{"type": "text", "text": "こんにちは"}]}]}', 0, 0, '{\"track1\": \"Song A\", \"track2\": \"Song B\", \"en1\": \"Encore Song\"}', 'sample-slug', 'PUBLISHED', 'tag1,tag2', 'https://example.com/thumbnail.jpg', 'Sample Blog Title', 0, 2),
	(3, '2024-10-05 12:41:43.000000', '3', '2024-10-05 12:41:43.000000', '3', '2024-10-05 12:41:43.000000', '2024-10-05 12:41:43.000000', 'REPORT', 0, '{"type": "doc", "content": [{"type": "paragraph", "content": [{"type": "text", "text": "こんにちは"}]}]}', 0, 0, '{\"track1\": \"Song A\", \"track2\": \"Song B\", \"en1\": \"Encore Song\"}', 'sample-slug Jhon', 'PUBLISHED', 'tag1,tag2', 'https://example.com/thumbnail.jpg', 'Blog Of John', 0, 3),
	(4, '2024-10-05 12:42:08.000000', '4', '2024-10-05 12:42:08.000000', '4', '2024-10-05 12:42:08.000000', '2024-10-05 12:42:08.000000', 'REPORT', 0, '{"type": "doc", "content": [{"type": "paragraph", "content": [{"type": "text", "text": "こんにちは"}]}]}', 0, 0, '{\"track1\": \"Song A\", \"track2\": \"Song B\", \"en1\": \"Encore Song\"}', 'sample-slug Mike', 'PUBLISHED', 'tag1,tag2', 'https://example.com/thumbnail.jpg', 'Blog Of Mike', 0, 4);
INSERT INTO `comments` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `comment_created_time`, `comment_updated_time`, `content`, `is_deleted`, `author_id`, `blog_id`)
VALUES
	(1, '2024-10-05 13:23:35.000000', 'System', '2024-10-05 13:23:35.000000', 'System', '2024-10-05 13:23:35.000000', '2024-10-05 13:23:35.000000', 'This is a sample comment.', 0, 3, 1);

INSERT INTO `notifications` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `is_deleted`, `is_read`, `notification_created_at`, `notification_type`, `read_at`, `blog_id`, `comment_id`, `target_user_id`, `trigger_user_id`)
VALUES
	(1, '2024-10-05 13:20:08.000000', 'System', '2024-10-05 13:20:08.000000', 'System', 0, 0, '2024-10-05 13:20:08.000000', 'FOLLOW', NULL, NULL, NULL, 2, 3),
	(2, '2024-10-05 13:20:51.000000', 'System', '2024-10-05 13:20:51.000000', 'System', 0, 0, '2024-10-05 13:20:51.000000', 'FOLLOW', NULL, NULL, NULL, 2, 4),
	(4, '2024-10-05 13:23:42.000000', 'System', '2024-10-05 13:23:42.000000', 'System', 0, 0, '2024-10-05 13:23:42.000000', 'COMMENT', NULL, 1, 1, 2, 3),
	(6, '2024-10-05 13:24:51.000000', 'System', '2024-10-05 13:24:51.000000', 'System', 0, 0, '2024-10-05 13:24:51.000000', 'BLOG_CREATED', NULL, 3, NULL, 2, 3);
