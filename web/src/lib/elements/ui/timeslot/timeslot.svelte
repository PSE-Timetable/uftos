<script lang="ts">
  export let begin: string;
  export let end: string;
  export let index: string;
</script>

<div class="flex flex-col w-min items-center gap-0.5 col-[1]">
  <div class="flex w-max">
    <p>{begin}</p>
  </div>

  <div class="flex flex-row gap-2 items-center pr-6">
    <p>{index}</p>
    <div>
      <!-- Vertical lines -->
      <svg fill="none" height="39" viewBox="0 0 2 39" width="2" xmlns="http://www.w3.org/2000/svg">
        <rect fill="#2B6E89" height="6" rx="0.5" width="1" x="0.5" />
        <rect fill="#2B6E89" height="6" rx="0.5" width="1" x="0.5" y="11" />
        <rect fill="#2B6E89" height="6" rx="0.5" width="1" x="0.5" y="22" />
        <rect fill="#2B6E89" height="6" rx="0.5" width="1" x="0.5" y="33" />
      </svg>
    </div>
  </div>

  <div class="flex w-max">
    <p>{end}</p>
  </div>
</div>
