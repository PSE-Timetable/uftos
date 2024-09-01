<script lang="ts">
  import type { Slot } from './lesson';

  export let title: Slot;
  export let leftCorner: Slot;
  export let rightCorner: Slot;
  export let length: number;

  export let color: string = '#EDB686';
</script>

<div
  style="background-color: {color};"
  class="row-span-{length} flex flex-col p-4 items-center rounded-lg gap-2 justify-between w-full h-full"
>
  {#if title.onClick}
    <button type="button" on:click={title.onClick}>{title.text}</button>
  {:else}
    <p>{title.text}</p>
  {/if}
  <div class="flex flex-row justify-between w-full gap-6">
    {#if leftCorner.onClick}
      <button type="button" on:click={leftCorner.onClick}>{leftCorner.text}</button>
    {:else}
      <p>{leftCorner.text}</p>
    {/if}
    {#if rightCorner.onClick}
      <button type="button" on:click={rightCorner.onClick}>{rightCorner.text}</button>
    {:else}
      <p>{rightCorner.text}</p>
    {/if}
  </div>
</div>
