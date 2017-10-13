package com.sucy.enchant.skillapi;

import com.rit.sucy.config.parse.DataSection;
import com.sucy.enchant.api.Cooldowns;
import com.sucy.enchant.api.CustomEnchantment;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.PassiveSkill;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillAttribute;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.api.skills.TargetSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * EnchantmentAPI © 2017
 * com.sucy.enchant.skillapi.SkillEnchantment
 */
public class SkillEnchantment extends CustomEnchantment {

    private static final String SKILL = "skill";

    private final Skill skill;

    SkillEnchantment(final String key, final DataSection data) {
        super(key, "No description provided");

        skill = SkillAPI.getSkill(data.getString(SKILL));
        if (skill == null) System.out.println(data.getString(SKILL) + " is not a skill");
        setMaxLevel(skill.getMaxLevel());
        Cooldowns.configure(settings,
                skill.getSettings().getBase(SkillAttribute.COOLDOWN),
                skill.getSettings().getScale(SkillAttribute.COOLDOWN));
    }

    @Override
    public void applyEquip(final LivingEntity user, final int level) {
        if (skill instanceof PassiveSkill) {
            ((PassiveSkill) skill).initialize(user, level);
        }
    }

    @Override
    public void applyUnequip(final LivingEntity user, final int level) {
        if (skill instanceof PassiveSkill) {
            ((PassiveSkill) skill).stopEffects(user, level);
        }
    }

    @Override
    public void applyInteractBlock(
            final Player user, final int level, final PlayerInteractEvent event) {
        if (skill instanceof SkillShot) {
            if (!Cooldowns.onCooldown(this, user, settings, level)) {
                if (((SkillShot) skill).cast(user, level)) {
                    Cooldowns.start(this, user);
                }
            }
        }
    }

    @Override
    public void applyInteractEntity(
            final Player user, final int level, final PlayerInteractEntityEvent event) {
        if (skill instanceof TargetSkill && event.getRightClicked() instanceof LivingEntity) {
            if (!Cooldowns.onCooldown(this, user, settings, level)) {
                final LivingEntity target = (LivingEntity)event.getRightClicked();
                if (((TargetSkill) skill).cast(user, target, level, SkillAPI.getSettings().isAlly(user, target))) {
                    Cooldowns.start(this, user);
                }
            }
        } else applyInteractBlock(user, level, null);
    }

    static final String SAVE_FOLDER = "enchant/skill/";

    @Override
    public String getSaveFolder() {
        return SAVE_FOLDER;
    }
}
