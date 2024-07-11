<script lang="ts">
  import WorkingHours from '$lib/components/working-hours/working-hours-day-container.svelte';
  import { WeekDay } from '$lib/utils';
</script>

123
<div class="h-[200px]"></div>
<div class="flex flex-col gap-2">
  <WorkingHours weekDay={WeekDay.MONDAY} />
  <WorkingHours weekDay={WeekDay.TUESDAY} />
  <WorkingHours weekDay={WeekDay.WEDNESDAY} />
  <WorkingHours weekDay={WeekDay.THURSDAY} />
  <WorkingHours weekDay={WeekDay.FRIDAY} />
</div>
