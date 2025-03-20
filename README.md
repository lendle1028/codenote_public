# Codenote: A Personality-Aware Programming Learning Tool

## Overview
Codenote is an Integrated Development Environment (IDE) designed to personalize programming education by adapting to students' personality traits. It enhances learning through features such as syntax highlighting, real-time error marking, and personality-based learning material recommendations.

## Key Features
- **Syntax Highlighting**: Improves code readability and comprehension.
- **Personality Assessment**: Adapts learning materials based on students' personality traits.
- **Real-time Error Marking**: Provides immediate, personalized feedback.
- **Pair Programming Support**: Facilitates collaboration between students.
- **Code Quality Assessment**: Evaluates code and suggests improvements.
- **AI-Powered Debugging**: Uses AI to detect and provide suggestions for fixing code errors.
- **Automated Personality Analysis**: Evaluates students' programming behaviors to assess personality traits and optimize learning strategies.

## System Architecture
Codenote is a web-based application with backend and frontend modules:
- **Backend**: Built with Java (JDK 17, Spring Framework), uses MySQL and follows a micro-service architecture.
- **Frontend**: Uses Monaco Editor for code and note editing, with a plugin-based architecture for modular functionality.
- **Personality and Code Quality Assessment**: Utilizes AI-based models to analyze students' behavior and provide feedback.
- **AI Integration**: Implements OpenRouter and Google API services via the PuppyChatter library to facilitate seamless AI interactions.

## Functionalities
### For Teachers
- Create code skeletons and learning materials
- Assess students' personality traits
- Configure code quality assessment parameters
- View overall learning and performance reports

### For Students
- Write and edit JavaScript/HTML code and notes
- Receive real-time error feedback
- Access personalized learning materials
- Participate in pair programming sessions
- Utilize AI-powered debugging assistance

## Installation and Setup
1. Unzip the provided package.
2. Add your OpenRouter keys and Google API keys to `application.properties`.
3. Import the provided SQL file into your MySQL database.
4. Deploy the backend using the provided JAR file with Apache Tomcat.
5. Launch the frontend via a web server.

## License
Codenote is released under the Apache License.

For more details, refer to the full research documentation.
