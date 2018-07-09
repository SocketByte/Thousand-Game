# Thousand
A simple multiplayer dice game based on [Game of 10000](https://en.wikipedia.org/wiki/Dice_10000)

It uses simplier score calculations and goes only to 1000 points.

## Features (coming soon)
- Everything in terminal, so you can play it even on your linux server!
- Fun colors and style
- Matchmaking service and manual IP input
- Great visuals (ASCII dices etc)

## Pictures (pre-alpha)
![](https://i.imgur.com/f8icNK1.png)

## Scoring
| Amount  | Dice value | Points |
|---------|------------|--------|
| 1x      | 1          | 10     |
| 1x      | 5          | 5      |
|         |            |        |
| 3x      | 1          | 100    |
| 4x      | 1          | 200    |
| 5x      | 1          | 400    |
| 3x      | 2          | 20     |
| 4x      | 2          | 40     |
| 5x      | 2          | 80     |
| 3x      | 3          | 30     |
| 4x      | 3          | 60     |
| 5x      | 3          | 120    |
| 3x      | 4          | 40     |
| 4x      | 4          | 80     |
| 5x      | 4          | 160    |
| 3x      | 5          | 50     |
| 4x      | 5          | 100    |
| 5x      | 5          | 150    |
| 3x      | 6          | 60     |
| 4x      | 6          | 120    |
| 5x      | 6          | 240    |

These are also scored:

| Dices         | Points |
|---------------|--------|
| 1, 2, 3, 4, 5 | 100    |
| 2, 3, 4, 5, 6 | 100    |

## Rules
You roll 5 dices. Some of your rolled combinations will be scored 
(based on [Scores](#scoring))

For example, you roll `2, 1, 5, 5, 3`
You have 3 total options to choose here:
- Take `1 x 1 = 10` and play with `4` (`2, 5, 5, 3`) dices
- Take `2 x 5 = 10` and play with `3` (`2, 1, 3`) dices
- Pass, **20 points** will be added to your account

##### Why 20 points for passing?
Because you will exchange `1 x 1 = 10` for **10 points**, and `2 x 5 = 10` for **10 points**

#### What if I chose to play further?
If you chose one of the **take** options, you will play again with dices shown above.
Everything will start again, for example you chose to play with `4` dices.
So you will roll another `4` dice values, like for example `2, 6, 3, 4`
and from this combination you don't gain nothing, so you lose the whole round!

#### I don't understand anything
Relax, these rules are not so easy to understand, just play it, you will catch the rules while playing!

If you played **Game of 10000** 
you should know most of the rules of **Thousand**.

## Game is not finished!
This is a prototype project, it is far from finishing, but I would like
to finish it!

## License
Project is fully open-source and licensed under MIT License.