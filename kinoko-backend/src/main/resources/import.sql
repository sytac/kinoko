INSERT INTO public.actions (id, name) VALUES (1, 'create_repo');
INSERT INTO public.actions (id, name) VALUES (2, 'set_project_as_finished');
INSERT INTO public.actions (id, name) VALUES (3, 'set_review_as_complete');
INSERT INTO public.actions (id, name) VALUES (4, 'archive_devcase');


INSERT INTO public.stages (id, name) VALUES (1, 'init');
INSERT INTO public.stages (id, name) VALUES (2, 'devcase_available_for_review');
INSERT INTO public.stages (id, name) VALUES (3, 'review_complete');
INSERT INTO public.stages (id, name) VALUES (4, 'devcase_archived');


INSERT INTO public.statuses (id, name) VALUES (1, 'init');
INSERT INTO public.statuses (id, name) VALUES (2, 'devcase_available_for_review');
INSERT INTO public.statuses (id, name) VALUES (3, 'review_complete');
INSERT INTO public.statuses (id, name) VALUES (4, 'devcase_archived');


INSERT INTO public.roles (id, name) VALUES (1, 'candidate');
INSERT INTO public.roles (id, name) VALUES (4, 'admin');
INSERT INTO public.roles (id, name) VALUES (5, 'creator');
INSERT INTO public.roles (id, name) VALUES (2, 'reviewer');
INSERT INTO public.roles (id, name) VALUES (3, 'generic_user');
INSERT INTO public.roles (id, name) VALUES (6, 'reviewers');


INSERT INTO public.templates (id, name, content, subject) VALUES (1, 'init_4candidate', 'Dear ${candidate.name},

Thank you for taking the time and effort to do the Sytac development case.

Soon you will receive an invite from GitHub to collaborate on the ${repository_url} repository. With this invite you can access the Sytac ${project_type} DevCase. All the necessary information, including the readme file, is in that repository.

When you have pushed your solution for the DevCase, please send an email to ${creator.name} (${creator.email}). One of our technical officers will then review your DevCase, and ${creator.name} will contact you to provide feedback.

The deadline for the assignment is: ${deadline}

If you have any technical questions, please contact <#list github_reviewers as github_reviewer>${github_reviewer.email} <#if github_reviewer_has_next>or</#if> </#list>.

Good luck,
${creator.name}', 'Repository created for ${candidate.githubUserName} | Sytac ${project_type} DevCase');
INSERT INTO public.templates (id, name, content, subject) VALUES (4, 'review_complete_4team', 'Dear ${creator.name},

${reviewer.name} has reviewed the DevCase of ${candidate.name}.

The feedback has been recorded in Google Forms and send to your email, or you can access it here: https://drive.google.com/drive/folders/0B_t5YTTCVeLwV3ZmdDFBQnJ2SDA

Regards
${reviewer.name}', 'The ${project_type} project of ${candidate.name} had been reviewed');
INSERT INTO public.templates (id, name, content, subject) VALUES (3, 'devcase_available_for_review_4team', 'Dear ${candidate.name},

${candidate.name} has submitted the ${project_type} DevCase and is now available for review.

You can access the DevCase here: ${repository_url}

Please provide your feedback via Google Forms:
<#if project_type == "backend">https://goo.gl/forms/IyrUooQdXzJi7HI83</#if>
<#if project_type == "frontend">https://goo.gl/forms/xfxrzMH1CrKGQGUj1</#if>

If you have any questions concerning the case, please contact ${candidate.name} directly.

Thanks
${creator.name}', 'The ${project_type} project of ${candidate.name} is available for review');
INSERT INTO public.templates (id, name, content, subject) VALUES (5, 'devcase_archived_4team', 'Dear admin,

the ${repository_url} has been archived.

Sytac', 'The ${project_type} project of ${candidate.name} had been archived');
INSERT INTO public.templates (id, name, content, subject) VALUES (2, 'init_4team', 'Hello,

A repository with <#if project_type == "frontend">a</#if><#if project_type == "backend">a</#if><#if project_type == "android">an</#if> ${project_type} DevCase has been created and send to ${candidate.name}.

You can access the repository via: ${repository_url}
The deadline for the DevCase is: ${deadline}

When the candidate finishes the assignment he will send an email to ${creator.name} (${creator.email}).

Sytac', 'A repository has been created for ${candidate.name}.');

INSERT INTO public.templates (id, name, content, subject) VALUES (6, 'deadline_in_3_days_4candidate', 'Dear ${candidate},

This is an automatic reminder from Sytac regarding your assignment that your deadline is in 3 days.', 'Deadline notification');

INSERT INTO public.templates (id, name, content, subject) VALUES (7, 'deadline_now_4team', 'Dear ${creator},

The deadline of ${candidate} has passed. Please check if he or she has completed the case and change the status to "solution available" or "archived".', 'Deadline passed');


INSERT INTO public.notification_map (id, role_id, stage_id, template_id) VALUES (191, 4, 2, 3);
INSERT INTO public.notification_map (id, role_id, stage_id, template_id) VALUES (192, 4, 3, 4);
INSERT INTO public.notification_map (id, role_id, stage_id, template_id) VALUES (260, 1, 1, 1);
INSERT INTO public.notification_map (id, role_id, stage_id, template_id) VALUES (261, 5, 1, 2);
INSERT INTO public.notification_map (id, role_id, stage_id, template_id) VALUES (264, 5, 4, 5);
INSERT INTO public.notification_map (id, role_id, stage_id, template_id) VALUES (263, 4, 4, 5);
INSERT INTO public.notification_map (id, role_id, stage_id, template_id) VALUES (281, 5, 2, 3);
INSERT INTO public.notification_map (id, role_id, stage_id, template_id) VALUES (286, 6, 2, 3);
INSERT INTO public.notification_map (id, role_id, stage_id, template_id) VALUES (430, 4, 1, 2);
INSERT INTO public.notification_map (id, role_id, stage_id, template_id) VALUES (452, 5, 3, 4);
INSERT INTO public.notification_map (id, role_id, stage_id, template_id) VALUES (453, 6, 3, 4);

-- REQUIREMENT
-- the name of the user with a role reviewer should be the github username
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (6, 'rick.heere@sytac.io', 'rickheere', 'Rick Heere', null, 2);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (300, 'tom.hastjarjanto@sytac.io', 'Intellicode', 'Tom Hastjarjanto', '2017-10-03 14:17:02.865000', 2);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (8, 'virgil.mormon@sytac.io', null, 'Virgil Mormon', null, 3);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (9, 'manuel.grassi@sytac.io', null, 'Manuel Grassi', null, 3);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (21, 'edgar.kraaikamp@sytac.io', 'edbaedba', 'Edgar Kraaikamp', null, 4);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (7, 'jack.tol@sytac.io', 'jacktol', 'Jack Tol', null, 2);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (5, 'jan.groot@sytac.io', 'JanGroot', 'Jan Groot', null, 2);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (461, 'devcase@sytac.io', 'sytac-bot', 'sytac-bot', '2017-10-04 13:12:54.827000', 2);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (500, 'yohandi_wijaya@yahoo.com', '255153YW', 'Yohandi Wijaya', '2017-10-06 12:59:44.592000', 1);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (520, 'matteo.lacognata@gmail.com', 'matteolc', 'Matteo La Cognata', '2017-10-10 08:12:09.032000', 1);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (4, 'marian.szabo@sytac.io', 'szabomarian', 'marian szabo', '2015-08-07 05:00:01.000000', 3);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (1, 'alessandro.berardinelli@sytac.io', 'aleberardinelli', 'alessandro berardinelli', '2015-08-07 05:00:01.000000', 3);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (540, 'havenchyk@gmail.com', 'havenchyk', 'Uladzimir Havenchyk ', '2017-10-16 13:53:42.894000', 1);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (570, 'udeste@gmail.com', 'Udeste', 'Umberto de Stefano', '2017-10-17 15:53:38.842000', 1);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (590, 'info@danielbaars.nl', 'danielbaars', 'Daniël Baars', '2017-10-18 13:48:53.926000', 1);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (610, 'bukinator.b@gmail.com', 'Ukibuki', 'Andrei Ponomarev', '2017-10-19 11:26:51.743000', 1);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (650, 's@grim.co.za', 'Siego', 'Siegfried Grimbeek', '2017-11-14 13:02:23.335000', 1);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (670, 'gabriele@gdpcons.com', 'gdelprete', 'Gabriele del Prete', '2017-11-15 19:19:16.796000', 1);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (10, 'sergey.firsov@sytac.io', 'sfirsov', 'Sergey Firsov', null, 2);
INSERT INTO public.users (id, email, github_user_name, name, timestamp, role_id) VALUES (256, 'ruben.fragoso@sytac.io', null, 'Ruben FragosoVirgil Mormon', null, 4);


INSERT INTO public.dev_cases (id, deadline, github_url, type, candidate_id, creator_id, reviewer_id, stage_id, creation, modified) VALUES (530, '2017-10-23 22:00:00.000000', 'https://github.com/Sytac-DevCase/sytac-frontend-assignment-matteolc', 'frontend', 520, 9, 9, 4, '2017-10-10 08:12:16.818000', '2017-10-17 15:53:23.578000');
INSERT INTO public.dev_cases (id, deadline, github_url, type, candidate_id, creator_id, reviewer_id, stage_id, creation, modified) VALUES (560, '2017-10-18 22:00:00.000000', 'https://github.com/Sytac-DevCase/sytac-frontend-assignment-szabomarian', 'frontend', 4, 21, null, 4, '2017-10-17 08:49:44.822000', '2017-10-18 07:46:06.460000');
INSERT INTO public.dev_cases (id, deadline, github_url, type, candidate_id, creator_id, reviewer_id, stage_id, creation, modified) VALUES (510, '2017-10-19 22:00:00.000000', 'https://github.com/Sytac-DevCase/sytac-android-assignment-255153YW', 'android', 500, 9, null, 4, '2017-10-06 12:59:51.907000', '2017-10-19 11:27:12.534000');
INSERT INTO public.dev_cases (id, deadline, github_url, type, candidate_id, creator_id, reviewer_id, stage_id, creation, modified) VALUES (630, '2017-10-20 22:00:00.000000', 'https://github.com/Sytac-DevCase/sytac-frontend-assignment-virgilmormon-0', 'frontend', 8, 8, null, 4, '2017-10-20 13:12:35.270000', '2017-10-20 13:14:20.992000');
INSERT INTO public.dev_cases (id, deadline, github_url, type, candidate_id, creator_id, reviewer_id, stage_id, creation, modified) VALUES (631, '2017-10-20 22:00:00.000000', 'https://github.com/Sytac-DevCase/sytac-frontend-assignment-virgilmormon', 'frontend', 8, 8, null, 4, '2017-10-20 13:13:39.688000', '2017-10-20 13:14:30.328000');
INSERT INTO public.dev_cases (id, deadline, github_url, type, candidate_id, creator_id, reviewer_id, stage_id, creation, modified) VALUES (620, '2017-10-30 23:00:00.000000', 'https://github.com/Sytac-DevCase/sytac-frontend-assignment-Ukibuki', 'frontend', 610, 9, null, 4, '2017-10-19 11:26:59.636000', '2017-11-01 08:30:44.820000');
INSERT INTO public.dev_cases (id, deadline, github_url, type, candidate_id, creator_id, reviewer_id, stage_id, creation, modified) VALUES (550, '2017-10-30 23:00:00.000000', 'https://github.com/Sytac-DevCase/sytac-frontend-assignment-havenchyk', 'frontend', 540, 9, null, 4, '2017-10-16 13:53:57.090000', '2017-11-01 10:40:28.428000');
INSERT INTO public.dev_cases (id, deadline, github_url, type, candidate_id, creator_id, reviewer_id, stage_id, creation, modified) VALUES (640, '2017-11-14 23:00:00.000000', 'https://github.com/Sytac-DevCase/sytac-frontend-assignment-havenchyk-0', 'frontend', 540, 9, null, 1, '2017-11-01 10:41:17.744000', '2017-11-01 10:41:17.744000');
INSERT INTO public.dev_cases (id, deadline, github_url, type, candidate_id, creator_id, reviewer_id, stage_id, creation, modified) VALUES (580, '2017-10-30 23:00:00.000000', 'https://github.com/Sytac-DevCase/sytac-frontend-assignment-Udeste', 'frontend', 570, 9, null, 4, '2017-10-17 15:53:47.134000', '2017-11-14 11:57:33.055000');
INSERT INTO public.dev_cases (id, deadline, github_url, type, candidate_id, creator_id, reviewer_id, stage_id, creation, modified) VALUES (660, '2017-11-29 23:00:00.000000', 'https://github.com/Sytac-DevCase/sytac-frontend-assignment-Siego', 'frontend', 650, 9, null, 1, '2017-11-14 13:02:32.554000', '2017-11-14 13:02:32.554000');
INSERT INTO public.dev_cases (id, deadline, github_url, type, candidate_id, creator_id, reviewer_id, stage_id, creation, modified) VALUES (680, '2017-11-29 23:00:00.000000', 'https://github.com/Sytac-DevCase/sytac-backend-assignment-gdelprete', 'backend', 670, 9, null, 1, '2017-11-15 19:19:23.077000', '2017-11-15 19:19:23.077000');
INSERT INTO public.dev_cases (id, deadline, github_url, type, candidate_id, creator_id, reviewer_id, stage_id, creation, modified) VALUES (600, '2017-10-30 23:00:00.000000', 'https://github.com/Sytac-DevCase/sytac-frontend-assignment-danielbaars', 'frontend', 590, 9, 9, 4, '2017-10-18 13:49:02.166000', '2017-11-16 11:00:04.780000');
