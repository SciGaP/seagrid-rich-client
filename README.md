### SEAGrid Science Gateway

The Science and Engineering Application Grid (SEAGrid) allows researchers to easily use scientific applications deployed across a wide range of supercomputers, campus clusters, and computing cloud. SEAGrid features both a powerful desktop client and go-anywhere Web application. SEAGrid helps scientist create model inputs, simplifies access to computing resources, enables visualizations of outputs, and provides archives for simulation data.

The SEAGrid Rich Client Interface is a desktop application developed in JavaFX.

## Maven targets

# Generate the application
	1. First you need to build the latest copy of the master branch of Apache Airavata.
	2. Go to jamberoo-libs directory and execute mvn_install_libs.sh
	3. Execute `mvn -P update-deployment clean package` command from the top level of the repo.

# Deploy the application artifacts to your webserver

	1. mvn -P update-deployment exec:exec@deploy-app

# Build a native installer

	1. mvn -P update-deployment install
