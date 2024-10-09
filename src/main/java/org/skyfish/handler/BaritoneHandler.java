package org.skyfish.handler;

import baritone.api.BaritoneAPI;
import baritone.api.event.events.PathEvent;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import org.skyfish.util.helper.BaritoneEventListener;

public class BaritoneHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    public boolean pathing = false;

    public void initialize() {
        BaritoneAPI.getSettings().allowParkour.value = true;
        BaritoneAPI.getSettings().allowDiagonalDescend.value = true;
        BaritoneAPI.getSettings().allowDiagonalAscend.value = true;
        BaritoneAPI.getProvider().getPrimaryBaritone().getGameEventHandler().registerEventListener(new BaritoneEventListener());
    }
    
    public boolean isPathing() {
        if (pathing) {
            return BaritoneEventListener.pathEvent != PathEvent.AT_GOAL
                    && BaritoneEventListener.pathEvent != PathEvent.CALC_FAILED
                    && BaritoneEventListener.pathEvent != PathEvent.NEXT_CALC_FAILED
                    && BaritoneEventListener.pathEvent != PathEvent.CANCELED;
        }
        return false;
    }

    public boolean isWalkingToGoalBlock() {
        return isWalkingToGoalBlock(0.75);
    }

    public boolean isWalkingToGoalBlock(double nearGoalDistance) {
        if (pathing) {
            if (!mc.thePlayer.onGround) return true;
            double distance;
            Goal goal = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getGoal();
            if (goal instanceof GoalBlock) {
                GoalBlock goal1 = (GoalBlock) BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getGoal();
                distance = mc.thePlayer.getDistance(goal1.getGoalPos().getX() + 0.5f, mc.thePlayer.posY, goal1.getGoalPos().getZ() + 0.5);
            } else if (goal instanceof GoalNear) {
                GoalNear goal1 = (GoalNear) BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getGoal();
                distance = mc.thePlayer.getDistance(goal1.getGoalPos().getX() + 0.5f, mc.thePlayer.posY, goal1.getGoalPos().getZ() + 0.5);
            } else {
                distance = goal.isInGoal(mc.thePlayer.getPosition()) ? 0 : goal.heuristic();
            }
            if (distance <= nearGoalDistance || BaritoneEventListener.pathEvent == PathEvent.AT_GOAL) {
                BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
                pathing = false;
                return false;
            }
            return BaritoneEventListener.pathEvent != PathEvent.CANCELED;
        }
        return false;
    }

    public boolean hasFailed() {
        if (BaritoneEventListener.pathEvent == PathEvent.CALC_FAILED
                || BaritoneEventListener.pathEvent == PathEvent.NEXT_CALC_FAILED) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
            pathing = false;
            return true;
        }
        return false;
    }

    public void walkToBlockPos(BlockPos blockPos) {
        PathingCommand pathingCommand = new PathingCommand(new GoalBlock(blockPos), PathingCommandType.REVALIDATE_GOAL_AND_PATH);
        BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().secretInternalSetGoalAndPath(pathingCommand);
        pathing = true;
    }

    public void walkCloserToBlockPos(BlockPos blockPos, int range) {
        PathingCommand pathingCommand = new PathingCommand(new GoalNear(blockPos, range), PathingCommandType.REVALIDATE_GOAL_AND_PATH);
        BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().secretInternalSetGoalAndPath(pathingCommand);
        pathing = true;
    }

    public void stopPathing() {
        BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
        pathing = false;
    }

    private static BaritoneHandler instance;
    public static BaritoneHandler getInstance() {
        if (instance == null) {
            instance = new BaritoneHandler();
        }

        return instance;
    }

}