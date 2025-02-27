//package cofh.lib.entity;
//
//import cofh.lib.util.helpers.MathHelper;
//import net.minecraft.core.particles.SimpleParticleType;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.phys.Vec3;
//
//import static cofh.core.init.CoreParticles.FROST;
//
//public class BlackHole extends AbstractAoESpell {
//
//    public BlackHole(EntityType<? extends BlackHole> type, Level level) {
//
//        super(type, level);
//        duration = 100;
//    }
//
//    public BlackHole(Level level, Vec3 pos, float radius) {
//
//        this(BLACK_HOLE.get(), level);
//        this.radius = radius;
//        moveTo(pos);
//    }
//
//    @Override
//    public void tick() {
//
//        // TODO particle
//        if (level.isClientSide) {
//            this.level.addParticle((SimpleParticleType) FROST.get(), this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
//        } else {
//            float rangeSqr = radius * radius;
//            double inv = 1 / rangeSqr;
//            for (Entity entity : level.getEntities(this, this.getBoundingBox().inflate(radius))) {
//                Vec3 diff = this.position().subtract(entity.position());
//                double distSqr = diff.lengthSqr();
//                if (distSqr < rangeSqr) {
//                    Vec3 velocity = entity.getDeltaMovement().scale(0.9F + distSqr * inv * 0.1F);
//                    entity.setDeltaMovement(velocity.add(diff.scale(0.05 * Math.min(MathHelper.invSqrt((float) distSqr), 1.0))));
//                }
//            }
//        }
//        super.tick();
//    }
//
//}
