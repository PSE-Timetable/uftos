<script lang="ts">
  import WorkingHours from '$lib/components/ui/working-hours/working-hours-day-container.svelte';
  import { WeekDay } from '$lib/utils';
</script>

123
<div class="h-[200px]"></div>
<div class="flex flex-col gap-2">
  <WorkingHours weekDay={WeekDay.monday} />
  <WorkingHours weekDay={WeekDay.tuesday} />
  <WorkingHours weekDay={WeekDay.wednesday} />
  <WorkingHours weekDay={WeekDay.thursday} />
  <WorkingHours weekDay={WeekDay.friday} />
</div>
