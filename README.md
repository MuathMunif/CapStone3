I mainly focused on Recruitment Opportunities and Clubs Management. Implemented endpoints

Add Swagger to provide automatic API documentation and an interactive UI for testing endpoints.

integration with minIO server to manage player CV file storage with secure presigned URLs.

Deployment "The project was containerized with Docker and Docker Compose, running the app, MySQL, and MinIO together".


The endpoints :

Accept a player into a club → /accept-player-to-club/{recruitmentOpportunity_id}/{requestJoining_id}

Reject a player from a club → /reject-player/{recruitmentOpportunity_id}/{requestJoining_id}

Close a recruitment opportunity → /close-recruitment-opportunity/{club_id}/{recruitmentOpportunity_id}

Get all recruitment opportunities DTO → /get-all-dto

Get recruitment opportunities by club ID → /get-all-by-club-id/{club_id}

Upload player CV → /upload-cv/{player_id}

Get club by ID → /get-club-by-id/{club_id}

Get club by ID (DTO) → /get-club-by-id-dto/{club_id}

Get all clubs DTO → /get-all-clubs-dto

Get clubs by location → /get-all-club-by-location/{location}

Get clubs by category → /get-all-club-by-category_id/{player_id}

Send qualification email to player → /qualified-email/{recruitmentOpportunity_id}/{player_id}/{club_id}



UML: 


<img width="834" height="345" alt="UML" src="https://github.com/user-attachments/assets/3df44ac2-ca62-4ede-8bbd-34c98d2c5ef0" />

