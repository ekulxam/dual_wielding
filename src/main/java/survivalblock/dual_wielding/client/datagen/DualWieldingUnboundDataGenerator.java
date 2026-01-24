package survivalblock.dual_wielding.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DualWieldingUnboundDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(DualWieldingUnboundParticleGenerator::new);
	}
}
