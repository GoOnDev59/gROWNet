\# GrowNet Backend API Documentation



\## 1. Overview



GrowNet provides REST APIs for authentication, internships, profile management, saved internships, internship matching, and career readiness.



\*\*Base URL:\*\* `http://localhost:8080`



\*\*API format:\*\* JSON



\## 2. Authentication



| Method | Endpoint | Description |

|---|---|---|

| POST | `/api/auth/register` | Register a new user |

| POST | `/api/auth/login` | Log in and receive an authentication response |



Registration and login request fields must match the backend DTO definitions.



For protected endpoints, include the access token in the request header when required:



`Authorization: Bearer <your-token>`



\## 3. Internship APIs



| Method | Endpoint | Description |

|---|---|---|

| GET | `/api/internships` | List verified internships |

| GET | `/api/internships/{id}` | Retrieve an internship by ID |

| POST | `/api/internships` | Create an internship |

| GET | `/api/internships/compare?ids=1,2` | Compare 2–4 internships |



\### Search and filtering



The internship listing endpoint supports optional query parameters:



\- `keyword` — search internship title, company, or description.

\- `location` — filter by location.

\- `skill` — filter by required skills.



Example:



`GET /api/internships?keyword=Java\&location=Mumbai\&skill=Spring`



You can combine filters or use any one of them independently.



\### Internship comparison



Example:



`GET /api/internships/compare?ids=1,2`



The comparison endpoint:

\- Accepts 2–4 distinct, positive internship IDs.

\- Returns the selected internships in the requested order.

\- Rejects invalid IDs and duplicate IDs.

\- Requires all requested internships to exist and be verified.



The response uses the internship response DTO, including deadline status.



\### Create an internship



Send a JSON request body matching `Dtos.InternshipRequest` to:



`POST /api/internships`



A successful creation returns HTTP `201 Created`.



\## 4. Profile APIs



| Method | Endpoint | Description |

|---|---|---|

| GET | `/api/profile` | Retrieve the authenticated user's profile |

| PUT | `/api/profile` | Update the authenticated user's profile |



Profile operations use the authenticated user's identity. The update request must match `Dtos.ProfileUpdateRequest`.



\## 5. Saved Internship APIs



| Method | Endpoint | Description |

|---|---|---|

| GET | `/api/saved-internships` | List the user's saved internships |

| POST | `/api/saved-internships/{internshipId}` | Save an internship |

| DELETE | `/api/saved-internships/{internshipId}` | Remove a saved internship |



These operations are intended for authenticated students. The backend validates whether an internship can be saved and prevents duplicate saves.



\## 6. Internship Matching API



| Method | Endpoint | Description |

|---|---|---|

| GET | `/api/matching/{internshipId}` | Retrieve matching results for an internship |



Use the internship ID in the URL. Authentication and any additional request requirements are determined by the backend security configuration.



\## 7. Career Readiness API



| Method | Endpoint | Description |

|---|---|---|

| GET | `/api/readiness` | Retrieve the authenticated user's career-readiness information |



This endpoint is used to access the readiness functionality provided by GrowNet.



\## 8. Health Check API



| Method | Endpoint | Description |

|---|---|---|

| GET | `/health` | Check whether the backend service is running |



Example response:



```json

{

&#x20; "status": "UP",

&#x20; "service": "GrowNet backend"

}

```



\## 9. Common HTTP Status Codes



| Status | Meaning |

|---|---|

| `200 OK` | Request completed successfully |

| `201 Created` | Resource created successfully |

| `400 Bad Request` | Invalid request or parameters |

| `401 Unauthorized` | Authentication is required or invalid |

| `403 Forbidden` | Access is not permitted |

| `404 Not Found` | Requested resource was not found |

| `500 Internal Server Error` | Unexpected server error |



Actual status codes depend on the endpoint and the backend's exception handling.



\## 10. Running the Backend



From the project root, run:



```powershell

.\\backend\\mvnw.cmd -f .\\backend\\pom.xml spring-boot:run

```



The default local API base URL is `http://localhost:8080`, provided the application starts on its configured port.



\## 11. Testing



Run the backend test suite from the project root:



```powershell

.\\backend\\mvnw.cmd -f .\\backend\\pom.xml clean test

```



A successful run ends with `BUILD SUCCESS`.



\---



\*\*Project:\*\* GrowNet  

\*\*Module:\*\* Backend REST API  

\*\*Documentation:\*\* API endpoint reference



