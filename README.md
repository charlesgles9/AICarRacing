## Architecture

- **7 Input Neurons:-** of lines/rays projected in 7 different directions/angles of projection.

``` kotlin

  val angles= arrayOf(180f,135f,90f,45f,0f)
```

- **4 Hidden Neurons**

- **2 Output Neurons**
    - The output velocity of the car.
    - The desired rotation.

```kotlin

 velocity=max_velocity- max_velocity*output[1].toFloat()
```

```kotlin

 angle+=if(output[0]>=0.5f) -1f else 1f
```

## Evolution

 This simulation uses the genetics algorithm inspired by darwinian Evolution with natural selection to evolve the network. Uses checkPoint markers to calculate the fitness function and a time limit for each lap.

 1. *The fitness function:-* calculated by counting the number of checkPoints passed by a given agent. You can also make this even more interesting by also keeping track of time each agent takes to finish a lap.

2.  *Selection:-* In this simulation 50% of the best cars are randomly chosen to breed and create identical copies of themselves to the next generation.<sup>( no cross breeding done for this project)</sup>

3.  *Mutation:-* All new children are given a 1% mutation rate. <sup>(can be any value but this worked best for me. Let trial and error be your guide)</sup>


## ScreenShots

![resize](https://user-images.githubusercontent.com/41951671/184492592-7eda0a78-6cff-4982-8423-655a59d56727.png)

![resize2](https://user-images.githubusercontent.com/41951671/184492732-25bbfc69-161d-4323-8727-6331741e872f.png)


