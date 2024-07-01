<script lang="ts">
  import * as Select from '$lib/elements/ui/select';
  import { WorkingHoursEnum, type TimeInterval } from '$lib/utils';

  const timesOfDay = Object.values(WorkingHoursEnum).map((time) => ({ value: time, label: time }));

  export let start: string = WorkingHoursEnum['10:00'];
  export let end: string = WorkingHoursEnum['10:00'];

  let selectedStart = { value: start, label: start };
  let selectedEnd = { value: end, label: end };

  export let onItemAdd: (item: TimeInterval) => void;
</script>

<div class="flex flex-row text-sm items-center gap-2 pr-2">
  <Select.Root bind:selected={selectedStart} portal={null}>
    <Select.Trigger>
      <Select.Value placeholder="Uhrzeit..." />
    </Select.Trigger>
    <Select.Content>
      <Select.Group>
        {#each timesOfDay as classItem}
          <Select.Item value={classItem.value} label={classItem.label}>
            {classItem.label}
          </Select.Item>
        {/each}
      </Select.Group>
    </Select.Content>
    <Select.Input name="chosenResource" />
  </Select.Root>

  bis

  <Select.Root bind:selected={selectedEnd} portal={null}>
    <Select.Trigger>
      <Select.Value placeholder="Uhrzeit..." />
    </Select.Trigger>
    <Select.Content>
      <Select.Group>
        {#each timesOfDay as classItem}
          <Select.Item value={classItem.value} label={classItem.label}>
            {classItem.label}
          </Select.Item>
        {/each}
      </Select.Group>
    </Select.Content>
    <Select.Input name="chosenResource" />
  </Select.Root>

  <div class="flex flex-row h-full gap-2">
    <svg
      on:click={() => onItemAdd({ start: start, end: end })}
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <g clip-path="url(#clip0_150_8642)">
        <circle cx="12" cy="12" r="11" stroke="#2B6E89" stroke-width="2" />
        <rect x="11" y="6" width="2" height="12" rx="1" fill="#2B6E89" />
        <rect x="6" y="13" width="2" height="12" rx="1" transform="rotate(-90 6 13)" fill="#2B6E89" />
      </g>
      <defs>
        <clipPath id="clip0_150_8642">
          <rect width="24" height="24" fill="white" />
        </clipPath>
      </defs>
    </svg>

    <svg width="20" height="24" viewBox="0 0 20 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path
        d="M17.0822 20.8544L17.7752 6.16939H18.7794C19.1912 6.16939 19.5229 5.82782 19.5229 5.41596C19.5229 5.00411 19.1912 4.65225 18.7794 4.65225H14.3899V3.14582C14.3899 1.70925 13.4158 0.775391 11.8691 0.775391H8.11223C6.56509 0.775391 5.6008 1.70968 5.6008 3.14539V4.65225H1.23194C0.830373 4.65225 0.478516 5.00368 0.478516 5.41596C0.478516 5.82739 0.829944 6.16939 1.23194 6.16939H2.23652L2.92909 20.8647C2.99937 22.3111 3.95337 23.2248 5.40023 23.2248H14.6111C16.0472 23.2248 17.0115 22.3008 17.0818 20.8548M7.20794 3.2461C7.20794 2.67353 7.63009 2.27196 8.23266 2.27196H11.7585C12.3714 2.27196 12.7931 2.67353 12.7931 3.2461V4.65225H7.20794V3.2461ZM5.55066 21.7184C4.9678 21.7184 4.53623 21.2761 4.50623 20.6941L3.80337 6.16939H16.1677L15.5051 20.6937C15.4751 21.2864 15.0529 21.718 14.4602 21.718L5.55066 21.7184ZM12.9838 19.9505C13.3151 19.9505 13.5667 19.6792 13.5765 19.2974L13.8778 8.69025C13.8877 8.30882 13.6267 8.02725 13.2851 8.02725C12.9739 8.02725 12.7125 8.31868 12.7022 8.68039L12.4014 19.2974C12.3911 19.6591 12.6422 19.9505 12.9838 19.9505ZM7.02752 19.9505C7.36909 19.9505 7.6198 19.6591 7.60994 19.2974L7.30866 8.68039C7.29837 8.31868 7.02752 8.02725 6.71594 8.02725C6.37437 8.02725 6.12323 8.30882 6.13309 8.69025L6.44466 19.2974C6.45494 19.6792 6.6958 19.9505 7.02752 19.9505ZM10.6134 19.2974V8.68039C10.6134 8.31868 10.3421 8.02725 10.0104 8.02725C9.67909 8.02725 9.39752 8.31868 9.39752 8.68039V19.2974C9.39752 19.6591 9.67909 19.9505 10.0104 19.9505C10.3318 19.9505 10.6134 19.6591 10.6134 19.2974Z"
        fill="#2B6E89"
      />
    </svg>
  </div>
</div>
