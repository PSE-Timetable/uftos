<script lang="ts">
  import { onMount } from 'svelte';
  import { Button } from '../button';

  export let text: string;
  export let url: string;
  let active: boolean = false;

  onMount(() => {
    active = window.location.pathname === url;
  });
</script>

<Button
  on:click={() => {
    window.location.href = url;
  }}
  class="{active
    ? 'bg-white text-primary'
    : 'text-white'}  text-lg px-6 py-4 hover:bg-accent hover:text-white hover:text rounded-full font-normal"
  >{text}</Button
>
