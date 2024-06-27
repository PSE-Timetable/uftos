<script lang="ts">
  let title: string = 'Title';
  export { title };

</script>

<div class="h-[10%] bg-primary text-white p-4 font-medium text-2xl">
  { title }
  <slot/>
</div>
