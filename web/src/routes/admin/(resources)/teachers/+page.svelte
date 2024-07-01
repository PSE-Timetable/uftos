<script lang="ts">
  import WorkingHours from '$lib/components/ui/working-hours/working-hours-day-container.svelte';
  import { WeekdDay } from '$lib/utils';
</script>

123
<div class="h-[200px]"></div>
<div class="flex flex-col gap-2">
  <WorkingHours weekDay={WeekdDay.monday} />
  <WorkingHours weekDay={WeekdDay.tuesday} />
  <WorkingHours weekDay={WeekdDay.wednesday} />
  <WorkingHours weekDay={WeekdDay.thursday} />
  <WorkingHours weekDay={WeekdDay.friday} />
</div>
