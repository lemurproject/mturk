#!/bin/bash
#SBATCH -n 1 # Number of cores
#SBATCH -N 1 # 1 node requested
#SBATCH --mem=32000 # Memory - Use 32G
#SBATCH --time=0 # No time limit
#SBATCH -p gpu # Use 4 GPUs
#SBATCH --gres=gpu:4 # Use 4 GPUs
#SBATCH --nodelist=boston-2-31

module load anaconda3
source activate bert
python bert_flask.py
