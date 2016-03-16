### SEAGrid Science Gateway

The Science and Engineering Application Grid (SEAGrid) allows researchers to easily use scientific applications deployed across a wide range of supercomputers, campus clusters, and computing cloud. SEAGrid features both a powerful desktop client and go-anywhere Web application. SEAGrid helps scientist create model inputs, simplifies access to computing resources, enables visualizations of outputs, and provides archives for simulation data.

The SEAGrid Rich Client Interface is a desktop application developed in JavaFX.

## Maven targets

# Generate the application

	mvn -P update-deployment clean package

# Deploy the application artifacts to your webserver

	mvn -P update-deployment exec:exec@deploy-app

# Build a native installer

	mvn install