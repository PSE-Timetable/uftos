<script lang="ts">
  let begin: string = '7:45';
  let end: string = '8:30';
  let index: string = '1.';
  export { begin, end, index };
</script>

<div class="flex flex-col w-min items-center gap-0.5 col-[1]">
  <div class="flex w-max ">
    <p> { begin }</p>
  </div>

  <div class="flex flex-row gap-2">
    <div class=""><p> { index }</p></div>
    <div class="">
      <svg fill="none" height="39" viewBox="0 0 2 39" width="2" xmlns="http://www.w3.org/2000/svg">
        <rect fill="#2B6E89" height="6" rx="0.5" width="1" x="0.5" />
        <rect fill="#2B6E89" height="6" rx="0.5" width="1" x="0.5" y="11" />
        <rect fill="#2B6E89" height="6" rx="0.5" width="1" x="0.5" y="22" />
        <rect fill="#2B6E89" height="6" rx="0.5" width="1" x="0.5" y="33" />
      </svg>
    </div>
    <div><p class="invisible"> { index }</p></div>
  </div>

  <div class="flex w-max">
    <p> { end }</p>
  </div>
</div>
